package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppApiException;

public class TokenApiException extends AppApiException {
    public TokenApiException(String message) {
        super(message);
    }
}
