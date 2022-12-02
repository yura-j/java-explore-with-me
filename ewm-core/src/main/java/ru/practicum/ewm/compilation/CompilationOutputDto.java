package ru.practicum.ewm.compilation;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationOutputDto {

    private List<Event> events;
    private Long id;
    private String title;
    private Boolean pinned;
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
    @Builder
    @AllArgsConstructor
    public static class Event {
        private String annotation;
        private Category category;
        private Integer confirmedRequests;
        private String eventDate;
        private Long id;
        private Initiator initiator;
        private Boolean paid;
        private String title;
        private Integer views;
    }
}
