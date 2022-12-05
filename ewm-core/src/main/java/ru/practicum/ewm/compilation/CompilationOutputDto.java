package ru.practicum.ewm.compilation;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationOutputDto {

    private List<EventDto> events;
    private Long id;
    private String title;
    private Boolean pinned;

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
    @Builder
    @AllArgsConstructor
    public static class EventDto {
        private String annotation;
        private CategoryDto categoryDto;
        private Integer confirmedRequests;
        private String eventDate;
        private Long id;
        private InitiatorDto initiatorDto;
        private Boolean paid;
        private String title;
        private Integer views;
    }
}
