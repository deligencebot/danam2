package com.delbot.danam.domain.post.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class PostResponseDto {
  //
  @Data
  @Builder
  public static class Detail {
    private Long postId;
    private Long postNo;
    private String category;
    private String title;
    private String contents;
    private String writer;
    private Long hits;
    // private boolean isNotice;
    // private boolean isEdited;
    // private boolean isCommentable;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
  }

  @Data
  @Builder
  @AllArgsConstructor
  public static class Pages {
    private Long postId;
    private Long postNo;
    private String category;
    private String title;
    private String writer;
    private Long hits;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
  }
}
