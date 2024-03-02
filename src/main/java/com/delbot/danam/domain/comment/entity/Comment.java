package com.delbot.danam.domain.comment.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.post.entity.Post;

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
@Table(name = "tb_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Comment {
  //
  @Id
  @Column(name = "comment_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long commentId;

  private String contents;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Comment parent;

  private int depth;

  private boolean isUpdated;

  private boolean isDeleted;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> children = new ArrayList<>();

  @CreationTimestamp
  private LocalDateTime createdTime;

  @UpdateTimestamp
  private LocalDateTime updatedTime;

  @Builder
  public Comment(String contents, Member member, Post post, int depth, Comment parent) {
    this.contents = contents;
    this.member = member;
    this.post = post;
    this.depth = depth;
    this.parent = parent;
    this.isUpdated = false;
    this.isDeleted = false;
  }

  public void updateComment(String contents) {
    this.contents = contents;
    this.isUpdated = true;
  }

  public void updateIsDeleted() {
    this.isDeleted = true;
  }

  // Test Method
  public void updateParent(Comment comment) {
    this.parent = comment;
  }
}
