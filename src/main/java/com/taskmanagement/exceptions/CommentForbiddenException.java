package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppForbiddenException;

public class CommentForbiddenException extends AppForbiddenException {
    public CommentForbiddenException(String message) {
        super(message);
    }
}
