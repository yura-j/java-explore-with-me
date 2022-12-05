package ru.practicum.ewm.statistic.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException() {
        super();
    }
}
