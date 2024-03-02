package com.delbot.danam.global.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.ws.rs.core.NoContentException;

@RestControllerAdvice
public final class GlobalExceptionHandler {
	//
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiError> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus httpStatus = errorCode.defaultHttpStatus();
        ApiError apiError = ApiError.of(errorCode);

        return new ResponseEntity<>(apiError, httpStatus);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<?> handleNoContentException(NoContentException e) {
        return ResponseEntity.noContent().build();
    }
}