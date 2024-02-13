package com.delbot.danam.domain.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class CommentRequestDto {
  //
  @Data
  public static class Post {
    private String category;

    private Long postNo;

    @NotEmpty(message = "댓글을 입력하세요.")
    @Size(max = 1000, message = "댓글은 1000자까지 입력가능합니다.")
    private String contents;

    private Long parentId;
  }

  @Data
  public static class Update {
    private Long CommentId;

    private String contents;
  }

  @Data
  public static class Delete {
    private Long CommentId;
  }
}
