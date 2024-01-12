package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppApiException;

public class TaskApiException extends AppApiException {
    public TaskApiException(String message) {
        super(message);
    }
}
