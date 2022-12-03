package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.requests.Request;
import ru.practicum.ewm.requests.RequestRepository;
import ru.practicum.ewm.requests.RequestStatus;
import ru.practicum.ewm.statistic.client.StatisticService;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationHasEventsRepository compilationHasEventsRepository;
    private final RequestRepository requestRepository;

    private final EventService eventService;

    private final EntityManager em;
    private final StatisticService statisticService;

    @Transactional
    public CompilationOutputDto create(CompilationInputDto dto) {
        Compilation inputCompilation = Compilation
                .builder()
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .events(new ArrayList<>())
                .build();
        Compilation compilation = compilationRepository.save(inputCompilation);
        if (null != dto.getEvents()) {
            addEvents(dto.getEvents(), compilation);
        }
        List<Long> eventIds = getEventsIdsByCompilationIds(List.of(compilation.getId()));
        List<Request> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(RequestStatus.CONFIRMED, eventIds);

        em.clear();


        Map<Long, Integer> eventsViews = statisticService.getEventViews(eventIds, eventService.getMinDateByIds(eventIds));

        return CompilationMapper.toDto(
                compilationRepository
                        .findById(compilation.getId())
                        .orElseThrow(NotFoundException::new),
                confirmedRequests,
                eventsViews
        );
    }

    @Transactional
    public void delete(Long compilationId) {
        compilationRepository.deleteById(compilationId);
    }

    @Transactional
    public void deleteEvent(Long compilationId, Long eventId) {
        compilationHasEventsRepository.deleteByCompilationIdAndEventId(compilationId, eventId);
    }

    @Transactional
    public void addEvent(Long compilationId, Long eventId) {
        addEventByCompilationIdAndEventId(compilationId, eventId);
    }

    @Transactional
    public void pin(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(NotFoundException::new);
        compilation.setPinned(true);
    }

    @Transactional
    public void unpin(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(NotFoundException::new);
        compilation.setPinned(false);
    }

    private void addEvents(List<Long> eventsIds, Compilation compilation) {
        for (Long eventId :
                eventsIds) {
            addEventByCompilationIdAndEventId(compilation.getId(), eventId);
        }
    }

    private List<Long> getEventsIdsByCompilationIds(List<Long> compilationIds) {
        List<CompilationHasEvents> eventsRecords = compilationHasEventsRepository
                .findAllByCompilationIdIn(compilationIds);

        return eventsRecords
                .stream()
                .map(CompilationHasEvents::getEventId)
                .collect(Collectors.toList());
    }

    private void addEventByCompilationIdAndEventId(Long compilationId, Long eventId) {
        CompilationHasEvents compilationHasEvents = CompilationHasEvents
                .builder()
                .compilationId(compilationId)
                .eventId(eventId)
                .build();
        compilationHasEventsRepository.save(compilationHasEvents);
    }

    public List<CompilationOutputDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Compilation> compilations = null == pinned
                ? compilationRepository.findAll(page)
                : compilationRepository.findAllByPinned(pinned, page);
        List<Long> compilationIds =
                compilations
                        .stream()
                        .map(Compilation::getId)
                        .collect(Collectors.toList());
        List<Long> eventIds = getEventsIdsByCompilationIds(compilationIds);
        List<Request> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(RequestStatus.CONFIRMED, eventIds);
        Map<Long, Integer> eventsViews = statisticService.getEventViews(eventIds, eventService.getMinDateByIds(eventIds));

        return compilations
                .stream()
                .map(c -> CompilationMapper.toDto(c, confirmedRequests, eventsViews))
                .collect(Collectors.toList());
    }

    public CompilationOutputDto get(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(NotFoundException::new);
        List<Long> eventIds = getEventsIdsByCompilationIds(List.of(compilation.getId()));
        List<Request> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(RequestStatus.CONFIRMED, eventIds);

        Map<Long, Integer> eventsViews = statisticService.getEventViews(eventIds, eventService.getMinDateByIds(eventIds));

        return CompilationMapper.toDto(
                compilationRepository
                        .findById(compilation.getId())
                        .orElseThrow(NotFoundException::new),
                confirmedRequests,
                eventsViews
        );
    }
}
