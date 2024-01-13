package com.delbot.danam.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberLoginRequestDto {
    //
  @NotEmpty
  @Pattern(regexp = "^[a-z0-9]{8,20}$")
  private String name;

  @NotEmpty
  @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$") // 영문, 특수문자 8자 이상 20자 이하
  private String password;
}
