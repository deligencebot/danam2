package com.delbot.danam.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

public class AdminCategoryRequestDto {
  //
  @Data
  public static class Info {
    @NotBlank(message = "이름이 비어있습니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]{1,20}$"
            ,message = "20자이내의 한글, 영어, 숫자, 그리고 띄어쓰기만 입력이 가능합니다.")
    private String name;
    private String parent;
  } 

  @Data
  public static class Delete {
    private String name;
  }
}
