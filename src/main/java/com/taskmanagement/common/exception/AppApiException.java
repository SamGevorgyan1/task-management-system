package com.taskmanagement.common.exception;

public abstract class AppApiException  extends Exception {
    public AppApiException(String message) {
        super(message);
    }
}
