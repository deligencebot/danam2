package com.delbot.danam.domain.post.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.repository.PostRepository;
import com.delbot.danam.util.CustomTestUtils;

@ExtendWith(MockitoExtension.class)
public class PageServiceTest {
  //
  @InjectMocks
  PageService pageService;

  @Mock
  PostRepository postRepository;

  @Test
  @DisplayName("게시판 조회 테스트")
  void getPage_success() throws Exception {
    String category = "board";
    Page<Post> mockPage = CustomTestUtils.generateMockPage(category, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    given(postRepository.findByCategory(anyString(), any(Pageable.class)))
            .willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByCategory(category, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByCategory(anyString(), any(Pageable.class));
  }

  @Test
  @DisplayName("모든조건검색 테스트")
  void searchByAll_success() throws Exception {
    String category = "board";
    String keyword = "aaa";
    Page<Post> mockPage = CustomTestUtils.generateMockPage(category, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    given(postRepository.findByAllTarget(anyString(), anyString(), any(Pageable.class)))
            .willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByAllTarget(category, keyword, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByAllTarget(anyString(), anyString(), any(Pageable.class));
  }

  @Test
  @DisplayName("제목&내용 검색 테스트")
  void searchByTitleAndContents_success() throws Exception {
    String category = "board";
    String keyword = "aaa";
    Page<Post> mockPage = CustomTestUtils.generateMockPage(category, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    given(postRepository.findByTitleAndContents(anyString(), anyString(), any(Pageable.class)))
            .willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByTitleAndContents(category, keyword, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByTitleAndContents(anyString(), anyString(), any(Pageable.class));
  }

  @Test
  @DisplayName("제목 검색 테스트")
  void searchByTitle_success() throws Exception {
    String category = "board";
    String keyword = "aaa";
    Page<Post> mockPage = CustomTestUtils.generateMockPage(category, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    given(postRepository.findByTitle(anyString(), anyString(), any(Pageable.class)))
            .willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByTitle(category, keyword, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByTitle(anyString(), anyString(), any(Pageable.class));
  }
}
