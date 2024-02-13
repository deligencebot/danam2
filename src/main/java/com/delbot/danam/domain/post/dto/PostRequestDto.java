package com.delbot.danam.domain.post.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class PostRequestDto {
  //
  @Data
  public static class Post {
    @NotEmpty(message = "제목을 입력하세요.")
    @Size(max = 50, message = "제목이 너무 깁니다.")
    private String title;

    @NotEmpty(message = "내용을 입력하세요.")
    @Size(max = 5000, message = "내용은 5000자 까지 입력가능합니다.")
    private String contents;

    private boolean isNotice;

    private boolean isCommentable;
  }

  @Data
  public static class Update {
    @NotEmpty(message = "제목을 입력하세요.")
    @Size(max = 50, message = "제목이 너무 깁니다.")
    private String title;

    @NotEmpty(message = "내용을 입력하세요.")
    @Size(max = 5000, message = "내용은 5000자 까지 입력가능합니다.")
    private String contents;

    private boolean isNotice;

    private boolean isCommentable;

    private List<String> deleteFileUrls = new ArrayList<>();

    private List<String> deleteImageUrls = new ArrayList<>();
  }
}
