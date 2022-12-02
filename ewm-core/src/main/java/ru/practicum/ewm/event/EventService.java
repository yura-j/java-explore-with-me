package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.controllers.admin.AdminEventsController;
import ru.practicum.ewm.controllers.public_api.PublicEventsController;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.error.ValidationException;
import ru.practicum.ewm.requests.*;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    public static final Integer views = 999;
    private final EventRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    @PersistenceContext
    private final EntityManager em;

    @Transactional
    public EventOutputDto createEvent(EventCreateDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(NotFoundException::new);
        Event event = EventMapper.getFromDto(dto, user, category, EventState.PENDING);
        checkPublishData(event);
        System.out.println(event);
        Event savedEvent = eventsRepository.save(event);

        return EventMapper.toDto(savedEvent);
    }

    @Transactional
    public EventOutputDto editEvent(EventEditDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        Event event = eventsRepository.findById(dto.getEventId()).orElseThrow(NotFoundException::new);

        Boolean stateIsOk = event.getState() == EventState.PENDING || event.getState() == EventState.CANCELED;
        if (!stateIsOk) {
            throw new ValidationException("Изменять можно только отмененные события " +
                    "или события в состоянии ожидания модерации");
        }

        fillEventFromDto(event, dto);

        if (event.getState() == EventState.CANCELED) {
            event.setState(EventState.PENDING);
        }
        checkPublishData(event);

        Event savedEvent = eventsRepository.save(event);

        return EventMapper.toDto(savedEvent);
    }

    public List<EventOutputDto> getEvents(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Event> events = eventsRepository.findByInitiatorId(userId, page);

        return events
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventOutputDto cancelEvent(Long userId, Long eventId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(NotFoundException::new);
        if (event.getState() != EventState.PENDING) {
            throw new ValidationException("Отменить можно только событие в состоянии ожидания модерации.");
        }
        event.setState(EventState.CANCELED);

        return EventMapper.toDto(event);
    }

    public EventOutputDto getEvent(Long eventId, Long userId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(NotFoundException::new);

        return EventMapper.toDto(event);
    }

    public List<RequestDto> getRequests(Long userId, Long eventId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(NotFoundException::new);
        List<Request> requests = requestRepository.findAllByEventId(event.getId());

        return requests
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestDto confirmRequest(Long userId, Long eventId, Long requestId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(NotFoundException::new);
        Request request = requestRepository.findByIdAndEventId(requestId, eventId)
                .orElseThrow(NotFoundException::new);
        Integer participantsCount = requestRepository.countByIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ValidationException("если для события лимит заявок равен 0 или отключена пре-модерация заявок," +
                    " то подтверждение заявок не требуется");
        }

        if (participantsCount >= event.getParticipantLimit()) {
            throw new ValidationException("нельзя подтвердить заявку, если уже достигнут лимит" +
                    " по заявкам на данное событие");
        }
        request.setStatus(RequestStatus.CONFIRMED);
        Boolean isLastParticipant = participantsCount == event.getParticipantLimit() - 1;
        if (isLastParticipant) {
            requestRepository
                    .findAllByEventIdAndStatus(eventId, RequestStatus.PENDING)
                    .forEach(r -> r.setStatus(RequestStatus.CANCELED));
        }

        return RequestMapper.toDto(request);
    }

    @Transactional
    public RequestDto rejectRequest(Long userId, Long eventId, Long requestId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(NotFoundException::new);
        Request request = requestRepository.findByIdAndEventId(requestId, event.getId())
                .orElseThrow(NotFoundException::new);
        request.setStatus(RequestStatus.REJECTED);

        return RequestMapper.toDto(request);
    }

    private void checkPublishData(Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время события должны быть как минимум" +
                    " на 2 часа позже текущей даты и времени");
        }
    }

    @Transactional
    public EventOutputDto publishEvent(Long eventId) {
        Event event = eventsRepository.findById(eventId).orElseThrow(NotFoundException::new);
        if (event.getState() != EventState.PENDING) {
            throw new ValidationException("событие должно быть в состоянии ожидания публикации");
        }

        event.setPublishedOn(LocalDateTime.now());
        event.setState(EventState.PUBLISHED);

        if (event.getEventDate().minusHours(1).isBefore(event.getPublishedOn())) {
            throw new ValidationException("дата начала события должна быть не ранее чем за час от даты публикации");
        }

        return EventMapper.toDto(event, getParticipantsSize(eventId), views);
    }

    public EventOutputDto rejectEvent(Long eventId) {
        Event event = eventsRepository.findById(eventId).orElseThrow(NotFoundException::new);
        if (event.getState() == EventState.PUBLISHED) {
            throw new ValidationException("событие не должно быть опубликовано");
        }

        event.setState(EventState.CANCELED);

        return EventMapper.toDto(event, getParticipantsSize(eventId), views);
    }

    private Integer getParticipantsSize(Long eventId) {
        return requestRepository.countByIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    @Transactional
    public EventOutputDto editEventByAdmin(EventEditDto dto, Long eventId) {
        Event event = eventsRepository.findById(eventId).orElseThrow(NotFoundException::new);
        fillEventFromDto(event, dto);
        Event savedEvent = eventsRepository.save(event);

        return EventMapper.toDto(savedEvent);
    }

    private void fillEventFromDto(Event event, EventEditDto dto) {
        if (null != dto.getTitle()) {
            event.setTitle(dto.getTitle());
        }
        if (null != dto.getAnnotation()) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (null != dto.getDescription()) {
            event.setDescription(dto.getDescription());
        }
        if (null != dto.getCategory()) {
            Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(NotFoundException::new);
            event.setCategory(category);
        }
        if (null != dto.getEventDate()) {
            event.setEventDate(EventMapper.parseDate(dto.getEventDate()));
        }
        if (null != dto.getPaid()) {
            event.setPaid(dto.getPaid());
        }
        if (null != dto.getLocation()
                && null != dto.getLocation().getLat()
                && null != dto.getLocation().getLon()
        ) {
            event.setLat(dto.getLocation().getLat());
            event.setLon(dto.getLocation().getLon());
        }

        if (null != dto.getParticipantLimit()) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
    }

    public List<EventOutputDto> searchEvents(AdminEventsController.AdminEventsSearchParameters searchParams) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> fields = query.from(Event.class);
        CriteriaQuery<Event> select = query.select(fields);
        List<Predicate> predicates = new ArrayList<>();

        if (searchParams.getUserIds().isPresent()) {
            predicates.add(cb.in(fields
                    .get("initiatorId")
                    .in(searchParams.getUserIds())
            ));
        }
        if (searchParams.getCategoryIds().isPresent()) {
            predicates.add(cb.in(fields
                    .get("categoryId")
                    .in(searchParams.getCategoryIds())
            ));
        }
        if (searchParams.getStates().isPresent()) {
            predicates.add(cb.in(fields
                    .get("state")
                    .in(searchParams.getStates())
            ));
        }
        if (searchParams.getRangeStart().isPresent()
                && searchParams.getRangeEnd().isPresent()
        ) {
            predicates.add(cb.between(fields.get("eventDate"),
                    searchParams.getRangeStart().get(),
                    searchParams.getRangeEnd().get()
            ));
        }
        if (searchParams.getSize().isEmpty() || searchParams.getFrom().isEmpty()) {
            throw new ValidationException("Произошла ошибка");
        }
        select
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(fields.get("id")));

        List<Event> events = em
                .createQuery(select)
                .setFirstResult(searchParams.getFrom().get())
                .setMaxResults(searchParams.getSize().get())
                .getResultList();
        List<Long> eventIds = events
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<Request> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(RequestStatus.CONFIRMED, eventIds);

        return
                events
                        .stream()
                        .map(event -> {
                            long count = confirmedRequests
                                    .stream()
                                    .filter(request -> Objects.equals(request.getEvent().getId(), event.getId()))
                                    .count();
                            return EventMapper.toDto(event, Math.toIntExact(count), views);
                        })
                        .collect(Collectors.toList());

    }

    public List<EventOutputDto> searchPublicEvents(PublicEventsController.PublicEventsSearchParameters searchParams) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        CriteriaQuery<Event> select = query.select(root);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("state"), EventState.PUBLISHED));

        if (searchParams.getPaid().isPresent()) {
            predicates.add(cb.equal(root.get("paid"),
                    searchParams.getPaid().get())
            );
        }

        if (searchParams.getText().isPresent()) {
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("annotation")),
                            "%" + searchParams.getText().get().toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")),
                            "%" + searchParams.getText().get().toLowerCase() + "%")
            ));
        }

        if (searchParams.getCategoryIds().isPresent()) {
            predicates.add(cb.in(root
                    .get("categoryId")
                    .in(searchParams.getCategoryIds())
            ));
        }

        if (searchParams.getRangeStart().isPresent()
                && searchParams.getRangeEnd().isPresent()
        ) {
            predicates.add(cb.between(root.get("eventDate"),
                    searchParams.getRangeStart().get(),
                    searchParams.getRangeEnd().get()
            ));
        } else {
            predicates.add(cb.greaterThan(root.get("eventDate"),
                    LocalDateTime.now()
            ));
        }

        if (searchParams.getSize().isEmpty() || searchParams.getFrom().isEmpty()) {
            throw new ValidationException("Произошла ошибка");
        }

        select.where(predicates.toArray(new Predicate[0]));

        List<Event> events = em
                .createQuery(select)
                .setFirstResult(searchParams.getFrom().get())
                .setMaxResults(searchParams.getSize().get())
                .getResultList();
        List<Long> eventIds = events
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<Request> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(RequestStatus.CONFIRMED, eventIds);

        return
                events
                        .stream()
                        .map(event -> {
                            long count = confirmedRequests
                                    .stream()
                                    .filter(request -> Objects.equals(request.getEvent().getId(), event.getId()))
                                    .count();
                            return EventMapper.toDto(event, Math.toIntExact(count), views);
                        })
                        .filter(dto -> {
                            if (searchParams.getOnlyAvailable().isPresent()
                                    && searchParams.getOnlyAvailable().get()) {
                                return dto.getConfirmedRequests() < dto.getParticipantLimit();
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
    }

    public EventOutputDto findById(Long id) {
        Event event = eventsRepository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(NotFoundException::new);

        return EventMapper.toDto(event, getParticipantsSize(id), views);
    }
}
