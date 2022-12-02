package ru.practicum.ewm.controllers.public_api;

public enum EventsSortParam {
    EVENT_DATE,
    VIEWS;

    public static EventsSortParam from(String statusParameter) {
        try {
            return EventsSortParam.valueOf(statusParameter);
        } catch (Exception e) {
            return null;
        }
    }
}
