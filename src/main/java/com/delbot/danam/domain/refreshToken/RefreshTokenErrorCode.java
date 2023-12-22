package com.delbot.danam.domain.refreshToken;

import org.springframework.http.HttpStatus;

import com.delbot.danam.global.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RefreshTokenErrorCode implements ErrorCode {
  // 404 NOT_FOUND
  NOT_FOUND_REFRESH_TOKEN("토큰이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

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
    return new RefreshTokenException(this);
  }

  @Override
  public RuntimeException defaultException(Throwable cause) {
    return new RefreshTokenException(this, cause);
  }
  
}
