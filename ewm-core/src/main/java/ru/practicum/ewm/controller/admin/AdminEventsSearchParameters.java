package ru.practicum.ewm.controller.admin;

import lombok.*;
import ru.practicum.ewm.event.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminEventsSearchParameters {


    private List<Long> userIds;
    private List<EventState> states;
    private List<Long> categoryIds;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;

    @Override
    public String toString() {
        return "EventsAdminSearchParameters{" +
                "userIds=" + userIds +
                ", states=" + states +
                ", categoryIds=" + categoryIds +
                ", rangeStart=" + rangeStart +
                ", rangeEnd=" + rangeEnd +
                ", from=" + from +
                ", size=" + size +
                '}';
    }
}