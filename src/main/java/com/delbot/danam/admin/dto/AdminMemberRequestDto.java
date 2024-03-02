package com.delbot.danam.admin.dto;

import lombok.Data;

public class AdminMemberRequestDto {
  //
  @Data
  public static class Ban {
    private Long memberId;
    private Long duration; // ms
  }

  @Data
  public static class Id {
    private Long memberId;
  }
}
