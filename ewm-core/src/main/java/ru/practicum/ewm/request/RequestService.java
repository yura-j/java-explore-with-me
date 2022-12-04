package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<RequestDto> getMyRequests(Long userId) {

        return requestRepository
                .findAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Не найдено"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найдено"));
        Boolean isAlreadyExists = requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent();
        if (isAlreadyExists) {
            throw new ValidationException("нельзя добавить повторный запрос");
        }

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("инициатор события не может добавить запрос на участие в своём событии");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("нельзя участвовать в неопубликованном событии");
        }

        RequestStatus status = event.getRequestModeration()
                ? RequestStatus.PENDING
                : RequestStatus.CONFIRMED;

        Request request = Request
                .builder()
                .requester(user)
                .event(event)
                .status(status)
                .build();
        Request savedRequest = requestRepository.save(request);

        Integer participantsCount = requestRepository.countByIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (participantsCount > event.getParticipantLimit()) {
            throw new ValidationException("у события достигнут лимит запросов на участие");
        }

        return RequestMapper.toDto(savedRequest);
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() -> new NotFoundException("Не найдено"));
        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toDto(request);
    }

}
