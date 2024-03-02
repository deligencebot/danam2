package com.delbot.danam.global.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
  // 400 BAD_REQUEST
  FILE_EMPTY("파일이 비어있습니다.", HttpStatus.BAD_REQUEST),
  // 401 UNAUTHORIZED
  RECAPTCHA_AUTHENTICATION_FAILURE("reCaptcha 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
  // 500 INTERNAL_SERVER_ERROR
  RECAPTCHA_SERVER_ERROR("reCaptcha 인증과정에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  UNKNOWN_ERROR("Unknown Error has occurred.", HttpStatus.INTERNAL_SERVER_ERROR);  
  
  private final String message;
  private final HttpStatus status;

  @Override
  public HttpStatus defaultHttpStatus() {
    return status;
  }

  @Override
  public String defaultMessage() {
    return message;
  }

  @Override
  public RuntimeException defaultException() {
    return new GlobalException(this);
  }

  @Override
  public RuntimeException defaultException(Throwable cause) {
    return new GlobalException(this, cause);
  }
}
