package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppApiException;

public class CommentApiException extends AppApiException {
    public CommentApiException(String message) {
        super(message);
    }
}
