package ru.practicum.ewm.controller.public_api;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicEventsSearchParameters {
    private String text;
    private List<Long> categoryIds;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private SortEventsParam sort;
    private Integer from;
    private Integer size;

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
