package ru.practicum.ewm.controller.public_api;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.event.EventOutputDto;
import ru.practicum.ewm.event.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
        return eventService.findById(id, ip);
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
        SortEventsParam sort = null == sortParam
                ? null
                : parseSort(sortParam);
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры from или size должны быть положительны");
        }

        PublicEventsSearchParameters searchParams = PublicEventsSearchParameters
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
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(dateParameter, format);
    }

    public enum SortEventsParam {
        EVENT_DATE,
        VIEWS;

        public static SortEventsParam from(String statusParameter) {
            try {
                return SortEventsParam.valueOf(statusParameter);
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PublicEventsSearchParameters {
        private Optional<String> text;
        private Optional<List<Long>> categoryIds;
        private Optional<Boolean> paid;
        private Optional<LocalDateTime> rangeStart;
        private Optional<LocalDateTime> rangeEnd;
        private Optional<Boolean> onlyAvailable;
        private Optional<SortEventsParam> sort;
        private Optional<Integer> from;
        private Optional<Integer> size;

        @Override
        public String toString() {
            return "EventsPublicSearchParameters{" +
                    "text=" + text +
                    ", categoryIds=" + categoryIds +
                    ", paid=" + paid +
                    ", rangeStart=" + rangeStart +
                    ", rangeEnd=" + rangeEnd +
                    ", onlyAvailable=" + onlyAvailable +
                    ", sort=" + sort +
                    ", from=" + from +
                    ", size=" + size +
                    '}';
        }
    }
}
