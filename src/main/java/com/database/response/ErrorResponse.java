package com.database.response;

import java.time.LocalDate;

public class ErrorResponse {

    private String message;
    private int statusCode;
    private LocalDate timestamp;
    private String path;

    public ErrorResponse(String message, int statusCode, LocalDate timestamp, String path) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }
}
