package com.delbot.danam.domain.post.dto;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import com.delbot.danam.domain.post.entity.PostImage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostImageDto {
  //
  private Long postImageId;

  private String imageUrl;

  private String storedFileName;

  private LocalDateTime createdTime;

  private Long postId;

  public static List<PostImageDto> convertDto(List<PostImage> postImageList) {
    List<PostImageDto> postImageDtoList = new ArrayList<>();

    postImageList.forEach(postImage -> {
      postImageDtoList.add(PostImageDto.builder()
              .postImageId(postImage.getPostImageId())
              .imageUrl(postImage.getImageUrl())
              .storedFileName(postImage.getStoredFileName())
              .createdTime(postImage.getCreatedTime())
              .postId(postImage.getPost().getPostId())
              .build());
    }); 

    return postImageDtoList;
  }
}
