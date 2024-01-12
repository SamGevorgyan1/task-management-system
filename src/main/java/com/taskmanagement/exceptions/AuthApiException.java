package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppApiException;

public class AuthApiException extends AppApiException {
    public AuthApiException(String message) {
        super(message);
    }
}
