package ru.practicum.ewm.event;

import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.util.DTFormat;

import java.time.LocalDateTime;

public class EventMapper {

    public static LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date, DTFormat.format);
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
        EventOutputDto.LocationDto locationDto = new EventOutputDto.LocationDto(savedEvent.getLat(),
                savedEvent.getLon());

        EventOutputDto.CategoryDto categoryDto = new EventOutputDto.CategoryDto(
                savedEvent.getCategory().getId(),
                savedEvent.getCategory().getName());
        EventOutputDto.InitiatorDto initiatorDto = new EventOutputDto.InitiatorDto(
                savedEvent.getInitiator().getId(),
                savedEvent.getInitiator().getName());

        String createdOn = savedEvent.getTimestamp().format(DTFormat.format);
        String publishedOn = "";
        if (null != savedEvent.getPublishedOn()) {
            publishedOn = savedEvent.getPublishedOn().format(DTFormat.format);
        }

        return EventOutputDto.builder()
                .id(savedEvent.getId())
                .annotation(savedEvent.getAnnotation())
                .description(savedEvent.getDescription())
                .eventDate(savedEvent.getEventDate().format(DTFormat.format))
                .category(categoryDto)
                .initiator(initiatorDto)
                .title(savedEvent.getTitle())
                .location(locationDto)
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
}
