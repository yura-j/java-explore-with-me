package ru.practicum.ewm.request;

import ru.practicum.ewm.util.DTFormat;

public class RequestMapper {
    public static RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .created(request.getTimestamp().format(DTFormat.format))
                .status(request.getStatus().toString())
                .build();
    }
}
