package com.codewithmosh.store.exceptions;

import java.time.LocalDateTime;

public class ApiError {

    private boolean success;
    private String message;
    private LocalDateTime timestamp;

    public ApiError(String message) {
        this.success = false;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
