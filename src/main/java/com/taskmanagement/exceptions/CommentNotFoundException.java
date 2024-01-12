package com.taskmanagement.exceptions;

import com.taskmanagement.common.exception.AppNotFoundException;

public class CommentNotFoundException extends AppNotFoundException {

    public CommentNotFoundException(String message) {
        super(message);
    }
}