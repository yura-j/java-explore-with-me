package ru.practicum.ewm.compilation;

import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.util.DTFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationOutputDto toDto(Compilation compilation,
                                             List<Request> confirmedRequests,
                                             Map<Long, Integer> eventViews) {
        List<CompilationOutputDto.EventDto> events = new ArrayList<>();
        if (null != compilation.getEvents()) {
            events = compilation.getEvents()
                    .stream()
                    .map(event -> {
                                User userInitiator = event.getInitiator();
                                CompilationOutputDto.InitiatorDto initiatorDto = new CompilationOutputDto.InitiatorDto(
                                        userInitiator.getId(),
                                        userInitiator.getName());
                                CompilationOutputDto.CategoryDto categoryDto = new CompilationOutputDto.CategoryDto(
                                        event.getCategory().getId(),
                                        event.getCategory().getName()
                                );
                                Integer confirmedCount = Math.toIntExact(confirmedRequests
                                        .stream()
                                        .filter(request -> Objects.equals(request.getEventId(), event.getId()))
                                        .count());
                                return CompilationOutputDto.EventDto
                                        .builder()
                                        .id(event.getId())
                                        .initiatorDto(initiatorDto)
                                        .eventDate(event.getEventDate().format(DTFormat.format))
                                        .title(event.getTitle())
                                        .paid(event.getPaid())
                                        .views(eventViews.getOrDefault(event.getId(), 0))
                                        .annotation(event.getAnnotation())
                                        .categoryDto(categoryDto)
                                        .confirmedRequests(confirmedCount)
                                        .build();
                            }
                    ).collect(Collectors.toList());
        }
        return CompilationOutputDto
                .builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(events)
                .build();
    }
}
