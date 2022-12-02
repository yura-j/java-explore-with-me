package ru.practicum.ewm.event;

import lombok.*;
import ru.practicum.ewm.markers.Base;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCreateDto {

    private String annotation;
    private Long category;
    @NotNull(groups = Base.class)
    private String description;
    private Integer confirmedRequests;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Location {
        private Float lon;
        private Float lat;
    }
}
