package com.taskmanagement.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Jacksonized
public class ExceptionResponse {

    private String message;
    private int statusCode;
    private List<String> details;

    public ExceptionResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public ExceptionResponse(String message, int statusCode, List<String> details) {
        this.message = message;
        this.statusCode = statusCode;
        this.details = details;
    }

    public ExceptionResponse(String message) {
        this.message = message;
    }
}