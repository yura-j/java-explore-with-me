package ru.practicum.ewm.controllers.public_api;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventsPublicSearchParameters {
    private Optional<String> text;
    private Optional<List<Long>> categoryIds;
    private Optional<Boolean> paid;
    private Optional<LocalDateTime> rangeStart;
    private Optional<LocalDateTime> rangeEnd;
    private Optional<Boolean> onlyAvailable;
    private Optional<EventsSortParam> sort;
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
