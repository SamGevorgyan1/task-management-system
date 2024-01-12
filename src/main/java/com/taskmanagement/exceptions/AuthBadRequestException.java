package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppBadRequestException;

public class AuthBadRequestException extends AppBadRequestException {
    public AuthBadRequestException(String message) {
        super(message);
    }
}
