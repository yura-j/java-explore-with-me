package ru.practicum.ewm.compilation;

import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.user.User;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static final DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static CompilationOutputDto toDto(Compilation compilation,
                                             List<Request> confirmedRequests,
                                             Map<Long, Integer> eventViews) {
        List<CompilationOutputDto.Event> events = null == compilation.getEvents()
                ? List.of()
                : compilation.getEvents()
                .stream()
                .map(event -> {
                            User userInitiator = event.getInitiator();
                            CompilationOutputDto.Initiator initiator = new CompilationOutputDto.Initiator(
                                    userInitiator.getId(),
                                    userInitiator.getName());
                            CompilationOutputDto.Category category = new CompilationOutputDto.Category(
                                    event.getCategory().getId(),
                                    event.getCategory().getName()
                            );
                            Integer confirmedCount = Math.toIntExact(confirmedRequests
                                    .stream()
                                    .filter(request -> Objects.equals(request.getEventId(), event.getId()))
                                    .count());
                            return CompilationOutputDto.Event
                                    .builder()
                                    .id(event.getId())
                                    .initiator(initiator)
                                    .eventDate(event.getEventDate().format(dtFormat))
                                    .title(event.getTitle())
                                    .paid(event.getPaid())
                                    .views(eventViews.getOrDefault(event.getId(), 0))
                                    .annotation(event.getAnnotation())
                                    .category(category)
                                    .confirmedRequests(confirmedCount)
                                    .build();
                        }
                ).collect(Collectors.toList());

        return CompilationOutputDto
                .builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(events)
                .build();
    }
}
