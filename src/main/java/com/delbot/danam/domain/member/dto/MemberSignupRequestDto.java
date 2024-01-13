package com.delbot.danam.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSignupRequestDto {
  //
  @NotEmpty
  @Pattern(regexp = "^[a-z0-9]{8,20}$",
          message = "아이디는 영어 소문자, 숫자포함 8글자부터 20글자까지 가능합니다.")
  private String name;

  @NotEmpty
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{7,16}$",
          message = "비밀번호는 영문+숫자+특수문자를 포함한 8~20자여야 합니다")
  private String password;

  @NotEmpty
  private String passwordCheck;

  @NotEmpty
  @Pattern(regexp = "^[a-zA-Z가-힣0-9\\\\s]{2,15}",
          message = "이름은 영문자, 한글, 숫자, 공백포함 2글자부터 15글자까지 가능합니다.")
  private String nickname;

  @NotEmpty
  @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
          message = "이메일 형식을 맞춰야합니다")
  private String email;
}
