package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppBadRequestException;

public class TaskBadRequestException extends AppBadRequestException {
    public TaskBadRequestException(String message) {
        super(message);
    }
}
