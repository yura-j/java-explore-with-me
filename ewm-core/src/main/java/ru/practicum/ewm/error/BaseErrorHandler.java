package ru.practicum.ewm.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order
@RestControllerAdvice
@Slf4j
public class BaseErrorHandler {
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleExceptionReturn400(final Throwable e) {
        log.info("400", e);

        return new ErrorResponse(e.getMessage(), 400);
    }
}
