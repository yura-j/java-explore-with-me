package ru.practicum.ewm.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEditDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Location {
        private Float lon;
        private Float lat;
    }

    private Long eventId;
    private String annotation;
    private Long category;
    private String description;
    private Integer confirmedRequests;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
}
