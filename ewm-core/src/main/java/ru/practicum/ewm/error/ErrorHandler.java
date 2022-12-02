package ru.practicum.ewm.error;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({NotFoundException.class, NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleExceptionReturn404(final RuntimeException e) {
        log.info("404{}", e.getMessage());
        return new ErrorResponse(e.getMessage(), 404);
    }

    @ExceptionHandler({AlreadyExistException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExceptionReturn409(final RuntimeException e) {
        log.info("409{}", e.getMessage());
        return new ErrorResponse(e.getMessage(), 409);
    }

    @ExceptionHandler(HasNoAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleExceptionReturn403(final RuntimeException e) {
        log.info("403{}", e.getMessage());
        return new ErrorResponse(e.getMessage(), 403);
    }
}
