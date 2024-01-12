package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppUnauthorizedOperationException;

public class CommentUnauthorizedOperationException extends AppUnauthorizedOperationException {
    public CommentUnauthorizedOperationException(String message) {
        super(message);
    }
}
