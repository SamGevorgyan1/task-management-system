package com.taskmanagement.common.exception;

public abstract class AppBadRequestException extends RuntimeException {
    public AppBadRequestException(String message) {
        super(message);
    }
}
