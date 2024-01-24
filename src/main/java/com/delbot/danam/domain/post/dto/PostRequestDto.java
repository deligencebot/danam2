package com.delbot.danam.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class PostRequestDto {
  //
  private String title;

  private String contents;

  private boolean isNotice;

  private boolean isCommentable;
}
