package com.taskmanagement.common.exception;

public abstract class AppForbiddenException extends RuntimeException{
    public AppForbiddenException(String message) {
        super(message);
    }
}