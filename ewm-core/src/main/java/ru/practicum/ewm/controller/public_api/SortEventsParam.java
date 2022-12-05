package ru.practicum.ewm.controller.public_api;

public enum SortEventsParam {
    EVENT_DATE,
    VIEWS;

    public static SortEventsParam from(String statusParameter) {
        try {
            return SortEventsParam.valueOf(statusParameter);
        } catch (Exception e) {
            return null;
        }
    }
}