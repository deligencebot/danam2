package com.delbot.danam.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.delbot.danam.domain.post.entity.Post;

public interface PostCustomRepository {
  //
  Page<Post> findByAllTarget(String category, String keyword, Pageable pageable);
  Page<Post> findByTitleAndContents(String category, String keyword, Pageable pageable);
  Page<Post> findByTitle(String category, String keyword, Pageable pageable);
  Page<Post> findByContents(String category, String keyword, Pageable pageable);
  Page<Post> findByWriter(String category, String keyword, Pageable pageable);
}
