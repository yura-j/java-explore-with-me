package ru.practicum.ewm.error;

public class HasNoAccessException extends RuntimeException {
    public HasNoAccessException(String message) {
        super(message);
    }
}

