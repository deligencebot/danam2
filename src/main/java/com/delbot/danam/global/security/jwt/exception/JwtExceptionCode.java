package com.delbot.danam.global.security.jwt.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtExceptionCode {
  //
  UNKNOWN_ERROR("UNKNOWN_ERROR", "UNKNOWN_ERROR"),
  NOT_FOUND_TOKEN("NOT_FOUND_TOKEN", "해더에서 토큰을 찾을 수 없음"),
  INVALID_TOKEN("INVALID_TOKEN", "잘못된 토큰"),
  EXPIRED_TOKEN("EXPIRED_TOKEN", "만료된 토큰"),
  UNSUPPORTED_TOKEN("UNSUPPORTED_TOKEN", "지원되지 않는 토큰");

  private final String code;
  private final String message;
}
