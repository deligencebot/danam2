package com.delbot.danam.domain.post.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PostRequestDto {
  //
  private String title;

  private String contents;

  // private boolean isNotice;

  // private boolean isEdited;

  // private boolean isCommentable;
}
