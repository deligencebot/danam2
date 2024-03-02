package com.delbot.danam.domain.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.delbot.danam.domain.category.Category;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
  //
  Page<Post> findAll(Pageable pageable);
  List<Post> findByCategory(Category category);
  Page<Post> findByCategory(Category category, Pageable pageable);
  Optional<Post> findByCategoryAndPostNo(Category category, Long postNo);
  List<Post> findByMember(Member member);
  @Modifying
  @Query(value = "UPDATE Post p SET p.hits=p.hits+1 WHERE p.postId=:postId")
  void updateHits(@Param("postId") Long postId);
}
