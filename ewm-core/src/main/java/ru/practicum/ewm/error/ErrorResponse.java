package ru.practicum.ewm.error;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    String error;
    Integer status;
    LocalDateTime datetime;

    public ErrorResponse(String error, Integer status) {
        this.error = error;
        this.status = status;
        datetime = LocalDateTime.now();
    }
}