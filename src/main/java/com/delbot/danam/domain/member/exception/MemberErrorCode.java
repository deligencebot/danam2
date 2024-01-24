package com.delbot.danam.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.delbot.danam.global.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode{
  // 400 BAD_REQUEST
  INVALID_VALUE("잘못된 값이 입력되었습니다.", HttpStatus.BAD_REQUEST),
  WRONG_PASSWORD_CHECK("비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  // 401 UNAUTHORIZED
  WRONG_PASSWORD("비밀번호가 틀렸습니다.", HttpStatus.UNAUTHORIZED),
  // 404 NOT_FOUND
  NOT_FOUND_MEMBER("회원이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  // 409 CONFLICT
  DUPLICATED_MEMBER("이미 사용중인 아이디입니다.", HttpStatus.CONFLICT),
  DUPLICATED_NICKNAME("이미 사용중인 별명입니다.", HttpStatus.CONFLICT),
  DUPLICATED_EMAIL("이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
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
    return new MemberException(this);
  }

  @Override
  public RuntimeException defaultException(Throwable cause) {
    return new MemberException(this, cause);
  }
}
