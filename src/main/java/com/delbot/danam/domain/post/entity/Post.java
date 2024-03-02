package com.delbot.danam.domain.post.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.delbot.danam.domain.category.Category;
import com.delbot.danam.domain.comment.entity.Comment;
import com.delbot.danam.domain.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Post {
  //
  @Id
  @Column(name = "post_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;

  private Long postNo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  private String title;

  private String contents;

  private Long hits;

  private boolean isNotice;

  private boolean isUpdated;

  private boolean isCommentable;

  @CreationTimestamp
  private LocalDateTime createdTime;

  @UpdateTimestamp
  private LocalDateTime updatedTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostImage> postImages = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostFile> postFiles = new ArrayList<>();

  @Builder
  public Post(Long postNo, Category category, String title, String contents, Member member) {
    this.postNo = postNo;
    this.category = category;
    this.title = title;
    this.contents = contents;
    this.hits = 0L;
    this.isNotice = false;
    this.isUpdated = false;
    this.isCommentable = true;
    this.member = member;
  }

  public void update(String title, String contents) {
    this.title = title;
    this.contents = contents;
    this.isUpdated = true;
  }

  public void updatePostSetting(boolean isNotice, boolean isCommentable) {
    this.isNotice = isNotice;
    this.isCommentable = isCommentable;
  }
}
