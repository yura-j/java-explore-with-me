package ru.practicum.ewm.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventOutputDto {

    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String description;
    private String createdOn;
    private String eventDate;
    private Long id;
    private InitiatorDto initiator;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private String state;
    private String title;
    private Integer views;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CategoryDto {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class InitiatorDto {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class LocationDto {
        private Float lat;
        private Float lon;
    }
}
