package com.taskmanagement.common.exception;

public abstract class AppResourceAlreadyExistsException extends RuntimeException{
    public AppResourceAlreadyExistsException(String message) {
        super(message);
    }
}
