package com.taskmanagement.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            AppBadRequestException.class,
            AppApiException.class,
            AppNotFoundException.class,
            AppResourceAlreadyExistsException.class,
            AppUnauthorizedOperationException.class
    })
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleCustomExceptions(Exception exception) {
        HttpStatus status;
        if (exception instanceof AppBadRequestException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof AppApiException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (exception instanceof AppNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (exception instanceof AppResourceAlreadyExistsException) {
            status = HttpStatus.CONFLICT;
        } else if (exception instanceof AppUnauthorizedOperationException) {
            status = HttpStatus.UNAUTHORIZED;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), status.value());
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> details = new ArrayList<>();

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }

        ExceptionResponse exceptionResponse = new ExceptionResponse("Invalid request", HttpStatus.BAD_REQUEST.value(), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }
}