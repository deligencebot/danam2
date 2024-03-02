package com.delbot.danam.domain.post.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tb_post_file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class PostFile {
  //
  @Id
  @Column(name = "post_file_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postFileId;

  private String fileUrl;

  private String storedFileName;

  private String originalFileName;

  private long fileSize;

  @CreationTimestamp
  private LocalDateTime createdTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  @Builder
  public PostFile(String fileUrl, String storedFileName, String originalFileName, long fileSize, Post post) {
    this.fileUrl = fileUrl;
    this.storedFileName = storedFileName;
    this.originalFileName = originalFileName;
    this.fileSize = fileSize;
    this.post = post;
  }
}
