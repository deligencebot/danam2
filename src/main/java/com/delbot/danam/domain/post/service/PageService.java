package com.delbot.danam.domain.post.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PageService {
  //
  private static final int PAGE_LIMIT = 20;

  private final PostRepository postRepository;

  @Transactional(readOnly = true)
  public Page<Post> getPage(Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findAll(PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postId"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByAll(String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByAllTarget(keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postId"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByTitleAndContents(String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByTitleAndContents(keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postId"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByTitle(String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByTitle(keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postId"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByContents(String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByContents(keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postId"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByWriter(String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByWriter(keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postId"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByComment(String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByComment(keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postId"));
  }

  @Transactional(readOnly = true)
  public Page<Post> getPage(String category, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByCategory(category, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postNo"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByAll(String category, String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByAllTarget(category, keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postNo"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByTitleAndContents(String category, String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByTitleAndContents(category, keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postNo"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByTitle(String category, String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByTitle(category, keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postNo"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByContents(String category, String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByContents(category, keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postNo"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByWriter(String category, String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByWriter(category, keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postNo"));
  }

  @Transactional(readOnly = true)
  public Page<Post> searchByComment(String category, String keyword, Pageable pageable) {
    int page = pageable.getPageNumber() - 1;
    return postRepository.findByComment(category, keyword, PageRequest.of(page, PAGE_LIMIT, Sort.Direction.DESC, "postNo"));
  }
}
