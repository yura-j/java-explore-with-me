package ru.practicum.ewm.event;

public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static EventState from(String statusParameter) {
        try {
            return EventState.valueOf(statusParameter);
        } catch (Exception e) {
            return null;
        }
    }
}
