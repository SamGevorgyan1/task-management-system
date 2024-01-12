package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppNotFoundException;

public class UserNotFoundException extends AppNotFoundException {

    public UserNotFoundException(String message){
        super(message);
    }
}