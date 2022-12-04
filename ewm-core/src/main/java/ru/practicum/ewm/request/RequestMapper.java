package ru.practicum.ewm.request;

import java.time.format.DateTimeFormatter;

public class RequestMapper {
    static DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .created(request.getTimestamp().format(dtFormat))
                .status(request.getStatus().toString())
                .build();
    }
}
