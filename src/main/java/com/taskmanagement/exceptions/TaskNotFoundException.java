package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppNotFoundException;

public class TaskNotFoundException extends AppNotFoundException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
