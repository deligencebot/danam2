package com.delbot.danam.domain.post.exception;

import org.springframework.http.HttpStatus;

import com.delbot.danam.global.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements ErrorCode {
  // 400 BAD_REQUEST
  INVALID_VALUE("잘못된 값이 입력되었습니다.", HttpStatus.BAD_REQUEST),
  // 401 UNAUTHORIZED
  UNAUTHORIZED_ACCESS("권한이 없는 유저입니다.", HttpStatus.UNAUTHORIZED),
  // 404 NOT_FOUND
  NOT_FOUND_POST("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

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
    return new PostException(this);
  }

  @Override
  public RuntimeException defaultException(Throwable cause) {
    return new PostException(this, cause);
  }
  
}
