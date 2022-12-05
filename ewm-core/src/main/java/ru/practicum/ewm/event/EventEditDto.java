package ru.practicum.ewm.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEditDto {

    private Long eventId;
    private String annotation;
    private Long category;
    private String description;
    private Integer confirmedRequests;
    private String eventDate;
    private LocationDto locationDto;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class LocationDto {
        private Float lon;
        private Float lat;
    }
}
