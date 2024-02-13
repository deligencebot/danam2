package com.delbot.danam.domain.comment.exception;

import org.springframework.http.HttpStatus;

import com.delbot.danam.global.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements ErrorCode {
  // 400 BAD_REQUEST
  INVALID_INPUT_VALUE("유효하지 않은 값이 입력되었습니다.", HttpStatus.BAD_REQUEST),
  // 401 UNAUTHORIZED
  UNAUTHORIZED_ACCESS("접근 권한이 없는 회원입니다.", HttpStatus.UNAUTHORIZED),
  // 404 NOT_FOUND
  NOT_FOUND_COMMENT("댓글이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  // 500 INTERNAL_SERVER_ERROR
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
    return new CommentException(this);
  }

  @Override
  public RuntimeException defaultException(Throwable cause) {
    return new CommentException(this, cause);
  }
  
}
