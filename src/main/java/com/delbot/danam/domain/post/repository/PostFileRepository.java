package com.delbot.danam.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delbot.danam.domain.post.entity.PostFile;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
  //
  PostFile findByFileUrl(String fileUrl);
}
