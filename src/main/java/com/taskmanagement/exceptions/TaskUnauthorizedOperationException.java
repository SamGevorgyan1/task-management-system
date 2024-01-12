package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppUnauthorizedOperationException;

public class TaskUnauthorizedOperationException extends AppUnauthorizedOperationException {
    public TaskUnauthorizedOperationException(String message) {
        super(message);
    }
}
