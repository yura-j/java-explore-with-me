package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventEditDto;
import ru.practicum.ewm.event.EventOutputDto;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.util.DTFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Slf4j
public class AdminEventsController {

    private final EventService eventService;

    @PatchMapping("/{eventId}/publish")
    public EventOutputDto publishEvent(
            @PathVariable Long eventId
    ) {
        log.info("Публикация события {}", eventId);

        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventOutputDto rejectEvent(
            @PathVariable Long eventId
    ) {
        log.info("Публикация события {}", eventId);

        return eventService.rejectEvent(eventId);
    }

    @PutMapping("/{eventId}")
    public EventOutputDto editEvent(
            @RequestBody EventEditDto dto,
            @PathVariable Long eventId
    ) {
        log.info("Отредактирован ивент");

        return eventService.editEventByAdmin(dto, eventId);
    }

    @GetMapping
    public List<EventOutputDto> searchEvents(
            @RequestParam(required = false) List<Long> userIds,
            @RequestParam(required = false) List<String> statesParameter,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) String rangeStartParameter,
            @RequestParam(required = false) String rangeEndParameter,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("Запрошены события");
        List<EventState> states;
        if (null == statesParameter) {
            states = null;
        } else {
            states = statesParameter
                    .stream()
                    .map(this::parseState)
                    .collect(Collectors.toList());
        }

        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры from или size должны быть положительны");
        }

        AdminEventsSearchParameters searchParams = AdminEventsSearchParameters
                .builder()
                .userIds(userIds)
                .states(states)
                .categoryIds(categoryIds)
                .rangeStart(parseDate(rangeStartParameter))
                .rangeEnd(parseDate(rangeEndParameter))
                .from(from)
                .size(size)
                .build();

        return eventService.searchEvents(searchParams);
    }

    private LocalDateTime parseDate(String dateParameter) {
        if (null == dateParameter) {
            return null;
        }

        return LocalDateTime.parse(dateParameter, DTFormat.format);
    }

    private EventState parseState(String s) {
        EventState status = EventState.from(s);
        if (status == null) {
            throw new IllegalArgumentException("Unknown state: " + s);
        }

        return status;
    }
}
