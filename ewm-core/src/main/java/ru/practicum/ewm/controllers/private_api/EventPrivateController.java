package ru.practicum.ewm.controllers.private_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventCreateDto;
import ru.practicum.ewm.event.EventEditDto;
import ru.practicum.ewm.event.EventOutputDto;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.markers.Base;
import ru.practicum.ewm.requests.RequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping
    public EventOutputDto createEvent(
            @RequestBody @Validated(Base.class) EventCreateDto dto,
            @PathVariable Long userId
    ) {
        log.info("Создан ивент");
        return eventService.createEvent(dto, userId);
    }

    @PatchMapping
    public EventOutputDto editEvent(
            @RequestBody EventEditDto dto,
            @PathVariable Long userId
    ) {
        log.info("Отредактирован ивент");
        return eventService.editEvent(dto, userId);
    }

    @PatchMapping("/{eventId}")
    public EventOutputDto cancelEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.info("Запрошены события");
        return eventService.cancelEvent(userId, eventId);
    }

    @GetMapping
    public List<EventOutputDto> getEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        log.info("Запрошены ивенты");
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventOutputDto getEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.info("Запрошен ивент");
        return eventService.getEvent(eventId, userId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getEvents(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.info("Запрошены заявки");
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{requestId}/confirm")
    public RequestDto confirmRequest(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long requestId) {
        log.info("Запрос на подтверждение заявки");
        return eventService.confirmRequest(userId, eventId, requestId);
    }

    @PatchMapping("/{eventId}/requests/{requestId}/reject")
    public RequestDto rejectRequest(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long requestId) {
        log.info("Запрос на отклонение заявки");
        return eventService.rejectRequest(userId, eventId, requestId);
    }

}
