package com.delbot.danam.global.security.jwt.util;

import lombok.Data;

@Data
public class LoginInfoDto {
  //
  private Long memberId;
  private String name;
  private String nickname;
}
