package com.delbot.danam.global.security.jwt.util;

public interface JwtProperties {
  //
  long ACCESS_EXPIRATION_TIME = 600 * 60 * 1000L;
  long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
  String TOKEN_PREFIX = "Bearer ";
  String HEADER_STRING = "Authorization";
}
