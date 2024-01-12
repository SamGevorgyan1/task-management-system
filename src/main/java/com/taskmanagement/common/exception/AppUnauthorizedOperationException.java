package com.taskmanagement.common.exception;

public abstract class AppUnauthorizedOperationException extends RuntimeException {

    public AppUnauthorizedOperationException(String message){
        super(message);
    }
}