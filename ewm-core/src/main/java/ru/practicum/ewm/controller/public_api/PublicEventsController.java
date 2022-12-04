package ru.practicum.ewm.controller.public_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventOutputDto;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.util.DTFormat;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Slf4j
public class PublicEventsController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public EventOutputDto get(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        log.info("Запрошено событие");
        String ip = request.getRemoteAddr();
        return eventService.getById(id, ip);
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
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request
    ) {
        log.info("Поиск события");
        SortEventsParam sort = null;
        if (sortParam != null) {
            sort = parseSort(sortParam);
        }

        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры from или size должны быть положительны");
        }

        PublicEventsSearchParameters searchParams = PublicEventsSearchParameters
                .builder()
                .text(text)
                .paid(paid)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .categoryIds(categoryIds)
                .rangeStart(parseDate(rangeStartParameter))
                .rangeEnd(parseDate(rangeEndParameter))
                .from(from)
                .size(size)
                .build();
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();

        return eventService.searchPublicEvents(searchParams, ip, uri);
    }


    private SortEventsParam parseSort(String sortParam) {
        SortEventsParam sort = SortEventsParam.from(sortParam);
        if (sort == null) {
            throw new IllegalArgumentException("Unknown state: " + sortParam);
        }

        return sort;
    }

    private LocalDateTime parseDate(String dateParameter) {
        if (null == dateParameter) {
            return null;
        }

        return LocalDateTime.parse(dateParameter, DTFormat.format);
    }
}
