package com.taskmanagement.common.exception;

public abstract class AppNotFoundException extends  RuntimeException {

    public AppNotFoundException(String message) {
        super(message);
    }
}
