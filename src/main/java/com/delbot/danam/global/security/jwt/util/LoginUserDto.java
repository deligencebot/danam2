package com.delbot.danam.global.security.jwt.util;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LoginUserDto {
  //
  private Long memberId;
  private String name;
  private String nickname;
  private List<String> roles = new ArrayList<>();

  public void addRole(String role) {
    roles.add(role);
  }
}
