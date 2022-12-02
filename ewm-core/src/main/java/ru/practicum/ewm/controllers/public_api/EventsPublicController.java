package ru.practicum.ewm.controllers.public_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.error.ValidationException;
import ru.practicum.ewm.event.EventOutputDto;
import ru.practicum.ewm.event.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Slf4j
public class EventsPublicController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public EventOutputDto get(
            @PathVariable Long id
    ) {
        return eventService.findById(id);
    }

    @GetMapping
    public List<EventOutputDto> searchEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sortParam,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) String rangeStartParameter,
            @RequestParam(required = false) String rangeEndParameter,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("Поиск события");
        EventsSortParam sort = null == sortParam
                ? null
                : parseSort(sortParam);
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры from или size должны быть положительны");
        }

        EventsPublicSearchParameters searchParams = EventsPublicSearchParameters
                .builder()
                .text(Optional.ofNullable(text))
                .paid(Optional.ofNullable(paid))
                .onlyAvailable(Optional.ofNullable(onlyAvailable))
                .sort(Optional.ofNullable(sort))
                .categoryIds(Optional.ofNullable(categoryIds))
                .rangeStart(Optional.ofNullable(parseDate(rangeStartParameter)))
                .rangeEnd(Optional.ofNullable(parseDate(rangeEndParameter)))
                .from(Optional.of(from))
                .size(Optional.of(size))
                .build();

        return eventService.searchPublicEvents(searchParams);
    }


    private EventsSortParam parseSort(String sortParam) {
        EventsSortParam sort = EventsSortParam.from(sortParam);
        if (sort == null) {
            throw new IllegalArgumentException("Unknown state: " + sortParam);
        }
        return sort;
    }

    private LocalDateTime parseDate(String dateParameter) {
        if (null == dateParameter) {
            return null;
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateParameter, format);
    }
}
