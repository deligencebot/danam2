package com.delbot.danam.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.delbot.danam.domain.comment.entity.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Data
public class CommentResponseDto {
  //
  private Long commentId;
  private String writer;
  private String contents;
  private int depth;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime createdTime;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime updatedTime;
  private boolean isUpdated;
  private boolean isDeleted;
  private List<CommentResponseDto> children = new ArrayList<>();

  @Builder
  public CommentResponseDto(Long commentId, String writer, String contents, int depth, LocalDateTime createdTime, LocalDateTime updatedTime, boolean isUpdated, boolean isDeleted) {
    this.commentId = commentId;
    this.writer = writer;
    this.contents = contents;
    this.depth = depth;
    this.createdTime = createdTime;
    this.updatedTime = updatedTime;
    this.isUpdated = isUpdated;
    this.isDeleted = isDeleted;
  }

  public static CommentResponseDto mapper(Comment comment) {
    return comment.isDeleted() ?
    CommentResponseDto.builder()
            .commentId(comment.getCommentId())
            .writer("\u0020")
            .contents("삭제된 댓글입니다.")
            .depth(comment.getDepth())
            .createdTime(comment.getCreatedTime())
            .updatedTime(comment.getUpdatedTime())
            .isUpdated(comment.isUpdated())
            .isDeleted(comment.isDeleted())
            .build()
            :
    CommentResponseDto.builder()
            .commentId(comment.getCommentId())
            .writer(comment.getMember().getNickname())
            .contents(comment.getContents())
            .depth(comment.getDepth())
            .createdTime(comment.getCreatedTime())
            .updatedTime(comment.getUpdatedTime())
            .isUpdated(comment.isUpdated())
            .isDeleted(comment.isDeleted())
            .build();
  }

  public static List<CommentResponseDto> listMapper(List<Comment> commentList) {
    List<CommentResponseDto> commentDtoList = new ArrayList<>();
    Map<Long, CommentResponseDto> commentDtoHashMap = new HashMap<>();
    
    commentList.forEach(comment -> {
      CommentResponseDto commentDto = CommentResponseDto.mapper(comment);
      commentDtoHashMap.put(commentDto.getCommentId(), commentDto);

      if (comment.getParent() != null) {
        commentDtoHashMap.get(comment.getParent().getCommentId()).getChildren().add(commentDto);
      } else {
        commentDtoList.add(commentDto);
      }
    });

    return commentDtoList;
  }
}
