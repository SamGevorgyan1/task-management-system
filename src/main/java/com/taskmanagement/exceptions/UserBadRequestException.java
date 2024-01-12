package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppBadRequestException;

public class UserBadRequestException extends AppBadRequestException {

    public UserBadRequestException(String message) {
        super(message);
    }
}
