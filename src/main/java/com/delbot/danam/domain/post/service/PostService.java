package com.delbot.danam.domain.post.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.exception.PostErrorCode;
import com.delbot.danam.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
  //
  private final PostRepository postRepository;

  @Transactional(readOnly = true)
  public Post getPost(String category, Long postNo) {
    Post post = postRepository.findByCategoryAndPostNo(category, postNo).orElseThrow(
      () -> PostErrorCode.NOT_FOUND_POST.defaultException()
    );
    return post;
  }

  @Transactional
  public Post addPost(Post post) {
    return postRepository.save(post);
  }

  @Transactional
  public void deletePost(Post post) {
    postRepository.delete(post);
  }

  @Transactional
  public void updateHits(Post post) {
    postRepository.updateHits(post.getPostId());
  }

  public Long initPostNo(String category) {
    return postRepository.findByCategory(category)
            .stream()
            .map(Post::getPostNo)
            .max(Comparator.naturalOrder())
            .orElse(0L) + 1L;
  }

  public List<Post> getMemberInfoPosts(Member member) {
    return postRepository.getMemberInfoPosts(member);
  }
}

