package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppBadRequestException;

public class CommentBadRequestException extends AppBadRequestException {
    public CommentBadRequestException(String message) {
        super(message);
    }
}
