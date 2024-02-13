package com.delbot.danam.domain.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.delbot.danam.domain.comment.entity.Comment;
import com.delbot.danam.domain.comment.exception.CommentErrorCode;
import com.delbot.danam.domain.comment.repository.CommentRepository;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.post.entity.Post;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
  //
  private final CommentRepository commentRepository;

  @Transactional(readOnly = true)
  public List<Comment> getComments(Post post) {
    return commentRepository.findByPost(post);
  }

  @Transactional
  public Comment saveComment(Comment comment) {
    return commentRepository.save(comment);
  }

  @Transactional
  public void deleteComment(Comment comment) {
    if (!CollectionUtils.isEmpty(comment.getChildren())) {
      comment.updateIsDeleted();
    } else {
      commentRepository.delete(getDeletableAncestorComment(comment));
    }
  }

  public Comment findById(Long id) {
    return commentRepository.findById(id).orElseThrow(
      () -> CommentErrorCode.NOT_FOUND_COMMENT.defaultException());
  }

  private Comment getDeletableAncestorComment(Comment comment) {
    Comment parent = comment.getParent();
    if (parent != null && parent.getChildren().size() == 1 && parent.isDeleted()) {
      return getDeletableAncestorComment(parent);
    }
    return comment;
  }

  public List<Comment> getMemberInfoComments(Member member) {
    return commentRepository.getMemberInfoComments(member);
  }
}
