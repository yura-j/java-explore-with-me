package ru.practicum.ewm.exception;

public class HasNoAccessException extends RuntimeException {
    public HasNoAccessException(String message) {
        super(message);
    }
}

