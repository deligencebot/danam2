package com.delbot.danam.domain.post.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.post.entity.Post;

public interface PostCustomRepository {
  // all category
  Page<Post> findByAllTarget(String keyword, Pageable pageable);
  Page<Post> findByTitleAndContents(String keyword, Pageable pageable);
  Page<Post> findByTitle(String keyword, Pageable pageable);
  Page<Post> findByContents(String keyword, Pageable pageable);
  Page<Post> findByWriter(String keyword, Pageable pageable);
  Page<Post> findByComment(String keyword, Pageable pageable);
  // one category
  Page<Post> findByAllTarget(String category, String keyword, Pageable pageable);
  Page<Post> findByTitleAndContents(String category, String keyword, Pageable pageable);
  Page<Post> findByTitle(String category, String keyword, Pageable pageable);
  Page<Post> findByContents(String category, String keyword, Pageable pageable);
  Page<Post> findByWriter(String category, String keyword, Pageable pageable);
  Page<Post> findByComment(String category, String keyword, Pageable pageable);
  // member information
  List<Post> getMemberInfoPosts(Member member);
}
