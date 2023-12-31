package com.delbot.danam.domain.member.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

public class MemberResponseDto {
    //
    @Data
    @Builder
    public static class Info {
      private Long memberId;
      private String name;
      private String nickname;
      private String email;
      private LocalDateTime createdDate;
    }
    
    @Data
    @Builder
    public static class Response {
      private String accessToken;
      private String refreshToken;
      private Long memberId;
      private String name;
      private String nickname;
      private String email;
      private LocalDateTime createdDate;
    }
}
