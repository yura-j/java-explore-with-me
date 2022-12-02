package ru.practicum.ewm.requests;

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
