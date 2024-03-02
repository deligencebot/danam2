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
@Table(name = "tb_post_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class PostImage {
  //
  @Id
  @Column(name = "post_image_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postImageId;

  private String imageUrl;

  private String storedFileName;

  private String originalFileName;

  private long fileSize;

  @CreationTimestamp
  private LocalDateTime createdTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  @Builder
  public PostImage(String imageUrl, String storedFileName, String originalFileName, long fileSize, Post post) {
    this.imageUrl = imageUrl;
    this.storedFileName = storedFileName;
    this.originalFileName = originalFileName;
    this.fileSize = fileSize;
    this.post = post;
  }
}
