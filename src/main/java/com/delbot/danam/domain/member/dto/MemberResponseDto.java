package com.delbot.danam.domain.member.dto;

import java.util.List;
import java.time.LocalDateTime;

import com.delbot.danam.domain.comment.dto.CommentResponseDto;
import com.delbot.danam.domain.post.dto.PostResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

public class MemberResponseDto {
    //
    @Data
    @Builder
    public static class Summary {
      private String name;
      private String nickname;
      private String email;
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
      private LocalDateTime createdDate;
    }
    
    @Data
    @Builder
    public static class Details {
      private String accessToken;
      private String refreshToken;
      private Long memberId;
      private String name;
      private String nickname;
      private String email;
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
      private LocalDateTime createdDate;
    }

    @Data
    @Builder
    public static class Token {
      private Long memberId;
      private String accessToken;
      private String refreshToken;
    }

    @Data
    @Builder
    public static class Info {
      private String name;
      private String nickname;
      private String email;
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
      private LocalDateTime createdDate;
      private List<PostResponseDto.Pages> memberPostList;
      private List<CommentResponseDto> memberCommentList; 
    }
}
