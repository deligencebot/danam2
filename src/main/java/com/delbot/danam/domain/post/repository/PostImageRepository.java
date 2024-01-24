package com.delbot.danam.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delbot.danam.domain.post.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
  //
  PostImage findByImageUrl(String imageUrl);
}
