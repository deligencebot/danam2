package com.delbot.danam.domain.post.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class PostUpdateRequestDto {
  //
  private String title;

  private String contents;

  private boolean isNotice;

  private boolean isCommentable;

  private List<String> deleteFileUrls = new ArrayList<>();

  private List<String> deleteImageUrls = new ArrayList<>();
}