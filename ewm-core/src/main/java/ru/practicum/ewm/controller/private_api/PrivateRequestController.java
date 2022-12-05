package ru.practicum.ewm.controller.private_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.RequestDto;
import ru.practicum.ewm.request.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Slf4j
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<RequestDto> getMyRequests(
            @PathVariable Long userId
    ) {
        log.info("Пользователь запросил свои заявки на участие в событиях id:{}", userId);

        return requestService.getMyRequests(userId);
    }

    @PostMapping
    public RequestDto createRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId
    ) {
        log.info("Пользователь{} создал запрос на участие в событии с id: {}", userId, eventId);

        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId
    ) {
        log.info("Пользователь{} создал запрос{} на участие в событии", userId, requestId);

        return requestService.cancelRequest(userId, requestId);
    }
}
