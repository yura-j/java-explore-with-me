package ru.practicum.ewm.event;

import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {

    public static final DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date, dtFormat);
    }

    public static Event getFromDto(EventCreateDto dto, User user, Category category, EventState state) {
        return Event.builder()
                .lat(dto.getLocation().getLat())
                .lon(dto.getLocation().getLon())
                .description(dto.getDescription())
                .annotation(dto.getAnnotation())
                .title(dto.getTitle())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .paid(dto.getPaid())
                .state(state)
                .eventDate(parseDate(dto.getEventDate()))
                .category(category)
                .initiator(user)
                .build();
    }

    public static EventOutputDto toDto(Event savedEvent, Integer confirmedRequests, Integer views) {
        EventOutputDto.Location location = new EventOutputDto.Location(savedEvent.getLat(),
                savedEvent.getLon());

        EventOutputDto.Category category = new EventOutputDto.Category(
                savedEvent.getCategory().getId(),
                savedEvent.getCategory().getName());
        EventOutputDto.Initiator initiator = new EventOutputDto.Initiator(
                savedEvent.getInitiator().getId(),
                savedEvent.getInitiator().getName());

        String createdOn = savedEvent.getTimestamp().format(dtFormat);
        String publishedOn = null == savedEvent.getPublishedOn()
                ? ""
                : savedEvent.getPublishedOn().format(dtFormat);
        return EventOutputDto.builder()
                .id(savedEvent.getId())
                .annotation(savedEvent.getAnnotation())
                .description(savedEvent.getDescription())
                .eventDate(savedEvent.getEventDate().format(dtFormat))
                .category(category)
                .initiator(initiator)
                .title(savedEvent.getTitle())
                .location(location)
                .createdOn(createdOn)
                .publishedOn(publishedOn)
                .confirmedRequests(confirmedRequests)
                .state(savedEvent.getState().toString())
                .views(views)
                .paid(savedEvent.getPaid())
                .requestModeration(savedEvent.getRequestModeration())
                .participantLimit(savedEvent.getParticipantLimit())
                .build();
    }

    public static EventOutputDto toDto(Event savedEvent) {
        return toDto(savedEvent, 0, 0);
    }
}
