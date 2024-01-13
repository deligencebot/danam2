package com.delbot.danam.domain.post.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.delbot.danam.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

  private String category;

  private String title;

  private String contents;

  private Long hits;

  // private boolean isNotice;

  // private boolean isEdited;

  // private boolean isCommentable;

  @CreationTimestamp
  private LocalDateTime createdTime;

  @UpdateTimestamp
  private LocalDateTime updatedTime;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @Builder
  public Post(Long postNo, String category, String title, String contents, Member member) {
    this.postNo = postNo;
    this.category = category;
    this.title = title;
    this.contents = contents;
    this.hits = 0L;
    this.member = member;
  }

  public void update(String title, String contents) {
    this.title = title;
    this.contents = contents;
  }
}
