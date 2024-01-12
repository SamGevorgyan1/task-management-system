package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppApiException;

public class UserApiException extends AppApiException {

    public UserApiException(String message){
        super(message);
    }
}