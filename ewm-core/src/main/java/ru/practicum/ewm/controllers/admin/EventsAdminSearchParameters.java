package ru.practicum.ewm.controllers.admin;

import lombok.*;
import ru.practicum.ewm.event.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventsAdminSearchParameters {


    private Optional<List<Long>> userIds;
    private Optional<List<EventState>> states;
    private Optional<List<Long>> categoryIds;
    private Optional<LocalDateTime> rangeStart;
    private Optional<LocalDateTime> rangeEnd;
    private Optional<Integer> from;
    private Optional<Integer> size;

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
