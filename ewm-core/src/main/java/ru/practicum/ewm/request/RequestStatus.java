package ru.practicum.ewm.request;

public enum RequestStatus {
    PENDING,
    CANCELED,
    REJECTED,
    CONFIRMED;


    public static RequestStatus from(String statusParameter) {
        try {
            return RequestStatus.valueOf(statusParameter);
        } catch (Exception e) {
            return null;
        }
    }
}
