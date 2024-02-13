package com.delbot.danam.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

public class MemberRequestDto {
  //
  @Data
  public static class Signup {
    @NotEmpty(message = "아이디는 필수 입력사항입니다.")
    @Pattern(regexp = "^[a-z0-9]{8,20}$",
            message = "아이디는 영어 소문자, 숫자포함 8글자부터 20글자까지 가능합니다.")
    private String name;

    @NotEmpty(message = "비밀번호는 필수 입력사항입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=]).{8,20}$",
            message = "비밀번호는 영문+숫자+특수문자를 포함한 8~20자여야 합니다")
    private String password;

    @NotEmpty(message = "비밀번호확인은 필수 입력사항입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=]).{8,20}$",
            message = "비밀번호는 영문+숫자+특수문자를 포함한 8~20자여야 합니다")
    private String passwordCheck;

    @NotEmpty(message = "별명은 필수 입력사항입니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9\\\\s]{2,15}",
            message = "이름은 영문자, 한글, 숫자, 공백포함 2글자부터 15글자까지 가능합니다.")
    private String nickname;

    @NotEmpty(message = "이메일 필수 입력사항입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
            message = "이메일 형식을 맞춰야합니다")
    private String email;
  }

  @Data
  public static class Login {
    @NotEmpty(message = "아이디는 필수 입력사항입니다.")
    @Pattern(regexp = "^[a-z0-9]{8,20}$",
            message = "아이디는 영어 소문자, 숫자포함 8글자부터 20글자까지 가능합니다.")
    private String name;
  
    @NotEmpty(message = "비밀번호는 필수 입력사항입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=]).{8,20}$",
            message = "비밀번호는 영문+숫자+특수문자를 포함한 8~20자여야 합니다")
    private String password;
  }

  @Data
  public static class Update {
    @NotEmpty(message = "별명은 필수 입력사항입니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9\\\\s]{2,15}",
            message = "이름은 영문자, 한글, 숫자, 공백포함 2글자부터 15글자까지 가능합니다.")
    private String nickname;

    @NotEmpty(message = "이메일은 필수 입력사항입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
            message = "이메일 형식을 맞춰야합니다")
    private String email;

    private String refreshToken;
  }

  @Data
  public static class AlterPassword {
    @NotEmpty(message = "기존비밀번호는 필수 입력사항입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=]).{8,20}$",
            message = "비밀번호는 영문+숫자+특수문자를 포함한 8~20자여야 합니다")
    private String existingPassword;

    @NotEmpty(message = "새비밀번호는 필수 입력사항입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=]).{8,20}$",
            message = "비밀번호는 영문+숫자+특수문자를 포함한 8~20자여야 합니다")
    private String newPassword;
    
    @NotEmpty(message = "비밀번호확인은 필수 입력사항입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=]).{8,20}$",
            message = "비밀번호는 영문+숫자+특수문자를 포함한 8~20자여야 합니다")
    private String newPasswordCheck;

    private String refreshToken;
  }

  @Data
  public static class CheckPassword {
    @NotEmpty(message = "비밀번호를 입력하세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=]).{8,20}$",
            message = "비밀번호는 영문+숫자+특수문자를 포함한 8~20자여야 합니다")
    private String password;
  }
}
