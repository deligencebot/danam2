package com.delbot.danam.admin.exception;

import org.springframework.http.HttpStatus;

import com.delbot.danam.global.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements ErrorCode {
  // 400 BAD_REQUEST
  INVALID_INPUT_VALUE("유효하지 않은 값이 입력되었습니다.", HttpStatus.BAD_REQUEST),
  NOT_BLOCKED_MEMBER("차단되지 않은 회원입니다.", HttpStatus.BAD_REQUEST),
  // 404 NOT_FOUND
  NOT_FOUND_CATEGORY("카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  // 409 CONFLICT
  OBJECT_HAS_CHILDREN("하위 카테고리가 있으면 삭제할 수 없습니다.", HttpStatus.CONFLICT),
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
    return new AdminException(this);
  }

  @Override
  public RuntimeException defaultException(Throwable cause) {
    return new AdminException(this, cause);
  }
}
