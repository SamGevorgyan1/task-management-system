package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppResourceAlreadyExistsException;

public class UserAlreadyExistsException extends AppResourceAlreadyExistsException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}