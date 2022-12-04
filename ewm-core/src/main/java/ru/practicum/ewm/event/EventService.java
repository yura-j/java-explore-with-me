package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.controller.admin.AdminEventsSearchParameters;
import ru.practicum.ewm.controller.public_api.PublicEventsSearchParameters;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.request.*;
import ru.practicum.ewm.statistic.client.ClientStatisticService;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.util.DTFormat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final ClientStatisticService clientStatisticService;
    @PersistenceContext
    private final EntityManager em;

    @Transactional
    public EventOutputDto createEvent(EventCreateDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найдено"));
        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(() -> new NotFoundException("Не найдено"));
        Event event = EventMapper.getFromDto(dto, user, category, EventState.PENDING);
        validPublishDataThrowException(event);
        Event savedEvent = eventsRepository.save(event);

        return EventMapper.toDto(savedEvent, 0, 0);
    }

    @Transactional
    public EventOutputDto editEvent(EventEditDto dto, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найдено"));
        Event event = eventsRepository.findById(dto.getEventId()).orElseThrow(() -> new NotFoundException("Не найдено"));

        Boolean stateIsOk = event.getState() == EventState.PENDING || event.getState() == EventState.CANCELED;
        if (!stateIsOk) {
            throw new ValidationException("Изменять можно только отмененные события " +
                    "или события в состоянии ожидания модерации");
        }

        fillEventFromDto(event, dto);

        if (event.getState() == EventState.CANCELED) {
            event.setState(EventState.PENDING);
        }
        validPublishDataThrowException(event);

        Event savedEvent = eventsRepository.save(event);

        Integer views = clientStatisticService
                .getEventViews(List.of(event.getId()), getMinDateByIds(List.of(event.getId())))
                .getOrDefault(event.getId(), 0);

        return EventMapper.toDto(savedEvent, getParticipantsSize(event.getId()), views);
    }

    public List<EventOutputDto> getEvents(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Event> events = eventsRepository.findByInitiatorId(userId, page);

        return getEventsOutputDto(events.stream(), events.stream());
    }

    @Transactional
    public EventOutputDto cancelEvent(Long userId, Long eventId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Не найдено"));
        if (event.getState() != EventState.PENDING) {
            throw new ValidationException("Отменить можно только событие в состоянии ожидания модерации.");
        }
        event.setState(EventState.CANCELED);

        Integer views = clientStatisticService
                .getEventViews(List.of(event.getId()), getMinDateByIds(List.of(event.getId())))
                .getOrDefault(event.getId(), 0);

        return EventMapper.toDto(event, getParticipantsSize(event.getId()), views);
    }

    public EventOutputDto getEvent(Long eventId, Long userId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Не найдено"));

        Integer views = clientStatisticService
                .getEventViews(List.of(event.getId()), getMinDateByIds(List.of(event.getId())))
                .getOrDefault(event.getId(), 0);

        return EventMapper.toDto(event, getParticipantsSize(event.getId()), views);
    }

    public List<RequestDto> getRequests(Long userId, Long eventId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Не найдено"));
        List<Request> requests = requestRepository.findAllByEventId(event.getId());

        return requests
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestDto confirmRequest(Long userId, Long eventId, Long requestId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Не найдено"));
        Request request = requestRepository.findByIdAndEventId(requestId, eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено"));
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
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException("Не найдено"));
        Request request = requestRepository.findByIdAndEventId(requestId, event.getId())
                .orElseThrow(() -> new NotFoundException("Не найдено"));
        request.setStatus(RequestStatus.REJECTED);

        return RequestMapper.toDto(request);
    }

    @Transactional
    public EventOutputDto publishEvent(Long eventId) {
        Event event = eventsRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено"));
        if (event.getState() != EventState.PENDING) {
            throw new ValidationException("событие должно быть в состоянии ожидания публикации");
        }

        event.setPublishedOn(LocalDateTime.now());
        event.setState(EventState.PUBLISHED);

        if (event.getEventDate().minusHours(1).isBefore(event.getPublishedOn())) {
            throw new ValidationException("дата начала события должна быть не ранее чем за час от даты публикации");
        }

        Integer views = clientStatisticService
                .getEventViews(List.of(eventId), getMinDateByIds(List.of(eventId)))
                .getOrDefault(eventId, 0);

        return EventMapper.toDto(event, getParticipantsSize(eventId), views);
    }

    @Transactional
    public EventOutputDto rejectEvent(Long eventId) {
        Event event = eventsRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено"));
        if (event.getState() == EventState.PUBLISHED) {
            throw new ValidationException("событие не должно быть опубликовано");
        }

        event.setState(EventState.CANCELED);

        Integer views = clientStatisticService
                .getEventViews(List.of(eventId), getMinDateByIds(List.of(eventId)))
                .getOrDefault(eventId, 0);

        return EventMapper.toDto(event, getParticipantsSize(eventId), views);
    }

    @Transactional
    public EventOutputDto editEventByAdmin(EventEditDto dto, Long eventId) {
        Event event = eventsRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено"));
        fillEventFromDto(event, dto);
        Event savedEvent = eventsRepository.save(event);

        Integer views = clientStatisticService
                .getEventViews(List.of(event.getId()), getMinDateByIds(List.of(event.getId())))
                .getOrDefault(event.getId(), 0);

        return EventMapper.toDto(savedEvent, getParticipantsSize(event.getId()), views);
    }

    public List<EventOutputDto> searchEvents(AdminEventsSearchParameters searchParams) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> fields = query.from(Event.class);
        CriteriaQuery<Event> select = query.select(fields);
        List<Predicate> predicates = new ArrayList<>();

        if (searchParams.getUserIds() != null) {
            predicates.add(cb.in(fields
                    .get("initiatorId")
                    .in(searchParams.getUserIds())
            ));
        }
        if (searchParams.getCategoryIds() != null) {
            predicates.add(cb.in(fields
                    .get("categoryId")
                    .in(searchParams.getCategoryIds())
            ));
        }
        if (searchParams.getStates() != null) {
            predicates.add(cb.in(fields
                    .get("state")
                    .in(searchParams.getStates())
            ));
        }
        if (searchParams.getRangeStart() != null
                && searchParams.getRangeEnd() != null
        ) {
            predicates.add(cb.between(fields.get("eventDate"),
                    searchParams.getRangeStart(),
                    searchParams.getRangeEnd()
            ));
        }
        if (searchParams.getSize() == null || searchParams.getFrom() == null) {
            throw new ValidationException("Произошла ошибка");
        }
        select
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(fields.get("id")));

        List<Event> events = em
                .createQuery(select)
                .setFirstResult(searchParams.getFrom())
                .setMaxResults(searchParams.getSize())
                .getResultList();

        return getEventsOutputDto(events.stream(), events.stream());

    }

    public List<EventOutputDto> searchPublicEvents(PublicEventsSearchParameters searchParams, String ip, String uri) {

        clientStatisticService.saveHit(uri, ip);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        CriteriaQuery<Event> select = query.select(root);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("state"), EventState.PUBLISHED));

        if (searchParams.getPaid() != null) {
            predicates.add(cb.equal(root.get("paid"),
                    searchParams.getPaid())
            );
        }

        if (searchParams.getText() != null) {
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("annotation")),
                            "%" + searchParams.getText().toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")),
                            "%" + searchParams.getText().toLowerCase() + "%")
            ));
        }

        if (searchParams.getCategoryIds() != null) {
            predicates.add(cb.in(root
                    .get("categoryId")
                    .in(searchParams.getCategoryIds())
            ));
        }

        if (searchParams.getRangeStart() != null
                && searchParams.getRangeEnd() != null
        ) {
            predicates.add(cb.between(root.get("eventDate"),
                    searchParams.getRangeStart(),
                    searchParams.getRangeEnd()
            ));
        } else {
            predicates.add(cb.greaterThan(root.get("eventDate"),
                    LocalDateTime.now()
            ));
        }

        if (searchParams.getSize() == null || searchParams.getFrom() == null) {
            throw new ValidationException("Произошла ошибка");
        }

        select.where(predicates.toArray(new Predicate[0]));

        List<Event> events = em
                .createQuery(select)
                .setFirstResult(searchParams.getFrom())
                .setMaxResults(searchParams.getSize())
                .getResultList();
        List<Long> eventIds = events
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<Request> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(RequestStatus.CONFIRMED, eventIds);

        Map<Long, Integer> eventsViews = clientStatisticService.getEventViews(eventIds,
                getMinDateByIds(eventIds));

        return
                events
                        .stream()
                        .map(event -> {
                            long count = confirmedRequests
                                    .stream()
                                    .filter(request -> Objects.equals(request.getEvent().getId(), event.getId()))
                                    .count();
                            return EventMapper.toDto(event,
                                    Math.toIntExact(count),
                                    eventsViews.getOrDefault(event.getId(), 0));
                        })
                        .filter(dto -> {
                            if (searchParams.getOnlyAvailable() != null
                                    && searchParams.getOnlyAvailable()) {
                                return dto.getConfirmedRequests() < dto.getParticipantLimit();
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
    }

    public EventOutputDto getById(Long id, String ip) {
        clientStatisticService.saveHit("/events/" + id, ip);

        Event event = eventsRepository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(() -> new NotFoundException("Не найдено"));

        Integer views = clientStatisticService
                .getEventViews(List.of(id), getMinDateByIds(List.of(id)))
                .getOrDefault(id, 0);

        return EventMapper.toDto(event, getParticipantsSize(id), views);
    }

    public String getMinDateByIds(List<Long> eventIds) {
        Optional<LocalDateTime> minDate = eventsRepository
                .findAllById(eventIds)
                .stream()
                .map(Event::getTimestamp)
                .min(LocalDateTime::compareTo);
        if (minDate.isEmpty()) {

            return LocalDateTime.now().format(DTFormat.format);
        }

        return minDate.get().format(DTFormat.format);
    }

    private List<EventOutputDto> getEventsOutputDto(Stream<Event> stream, Stream<Event> eventStream) {
        List<Long> eventIds = stream
                .map(Event::getId)
                .collect(Collectors.toList());

        List<Request> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(RequestStatus.CONFIRMED, eventIds);

        Map<Long, Integer> eventsViews = clientStatisticService.getEventViews(eventIds,
                getMinDateByIds(eventIds));

        return
                eventStream
                        .map(event -> {
                            long count = confirmedRequests
                                    .stream()
                                    .filter(request -> Objects.equals(
                                            request.getEvent().getId(),
                                            event.getId()))
                                    .count();

                            return EventMapper.toDto(event,
                                    Math.toIntExact(count),
                                    eventsViews.getOrDefault(event.getId(), 0));
                        })
                        .collect(Collectors.toList());
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
            Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(() -> new NotFoundException("Не найдено"));
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

    private Integer getParticipantsSize(Long eventId) {
        return requestRepository.countByIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private void validPublishDataThrowException(Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время события должны быть как минимум" +
                    " на 2 часа позже текущей даты и времени");
        }
    }
}
