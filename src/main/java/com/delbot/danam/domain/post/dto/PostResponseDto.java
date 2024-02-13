package com.delbot.danam.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.delbot.danam.domain.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

public class PostResponseDto {
  //
  @Data
  public static class Detail {
    private Long postId;
    private Long postNo;
    private String category;
    private String title;
    private String contents;
    private String writer;
    private Long hits;
    private List<PostFileDto> postFiles;
    private List<PostImageDto> postImages;
    private boolean isNotice;
    private boolean isUpdated;
    private boolean isCommentable;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedTime;

    @Builder
    public Detail(Post post) {
      this.postId = post.getPostId();
      this.postNo = post.getPostNo();
      this.category = post.getCategory();
      this.title = post.getTitle();
      this.contents = post.getContents();
      this.writer = post.getMember().getNickname();
      this.hits = post.getHits();
      this.postFiles = PostFileDto.convertDto(post.getPostFiles());
      this.postImages = PostImageDto.convertDto(post.getPostImages());
      this.isNotice = post.isNotice();
      this.isUpdated = post.isUpdated();
      this.isCommentable = post.isCommentable();
      this.createdTime = post.getCreatedTime();
      this.updatedTime = post.getUpdatedTime();
    }
  }

  @Data
  @Builder
  public static class Pages {
    private Long postId;
    private Long postNo;
    private String category;
    private String title;
    private String writer;
    private Long hits;
    private boolean isUpdated;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedTime;
  }
}
