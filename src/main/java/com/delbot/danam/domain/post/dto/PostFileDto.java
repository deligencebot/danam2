package com.delbot.danam.domain.post.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.delbot.danam.domain.post.entity.PostFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostFileDto {
  //
  private Long postFileId;

  private String fileUrl;

  private String storedFileName;

  private LocalDateTime createdTime;

  private Long postId;

  public static List<PostFileDto> convertDto(List<PostFile> postFileList) {
    List<PostFileDto> postFileDtoList = new ArrayList<>();

    postFileList.forEach(postFile -> {
      postFileDtoList.add(PostFileDto.builder()
              .postFileId(postFile.getPostFileId())
              .fileUrl(postFile.getFileUrl())
              .storedFileName(postFile.getStoredFileName())
              .createdTime(postFile.getCreatedTime())
              .postId(postFile.getPost().getPostId())
              .build());
    }); 

    return postFileDtoList;
  }
}
