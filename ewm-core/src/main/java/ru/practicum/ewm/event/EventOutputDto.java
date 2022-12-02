package ru.practicum.ewm.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventOutputDto {

    private String annotation;
    private Category category;
    private Integer confirmedRequests;
    private String description;
    private String createdOn;
    private String eventDate;
    private Long id;
    private Initiator initiator;
    private Location location;
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
    public static class Category {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Initiator {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Location {
        private Float lat;
        private Float lon;
    }
}
