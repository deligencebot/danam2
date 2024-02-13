package com.delbot.danam.domain.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delbot.danam.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {
  //
  List<Comment> findByContents(String contents);
}
