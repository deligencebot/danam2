package com.delbot.danam.domain.post.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
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

  String category;
  String keyword;
  Pageable pageable;
  Page<Post> mockPage;

  @BeforeEach
  void setup() {
    category = "fakeCategory";
    keyword = "fakeKeyword";
    pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "postNo");
    mockPage = CustomTestUtils.generateMockPage(category, pageable);
  }

  @Test
  @DisplayName("전체 게시판 게시글 조회 테스트")
  void getPage_success() throws Exception {
    given(postRepository.findAll(eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findAll(pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findAll(eq(pageable));
  }

  @Test
  @DisplayName("전체 게시판 게시글 검색(제목, 내용, 작성자) 테스트")
  void searchByAll_success() throws Exception {
    given(postRepository.findByAllTarget(eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByAllTarget(keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByAllTarget(eq(keyword), eq(pageable));
  }

  @Test
  @DisplayName("전체 게시판 게시글 검색(제목, 내용) 테스트")
  void searchByTitleAndContents_success() throws Exception {
    given(postRepository.findByTitleAndContents(eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByTitleAndContents(keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByTitleAndContents(eq(keyword), eq(pageable));
  }

  @Test
  @DisplayName("전체 게시판 게시글 조회(제목) 테스트")
  void searchByTitle_success() throws Exception {
    given(postRepository.findByTitle(eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByTitle(keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByTitle(eq(keyword), eq(pageable));
  }

  @Test
  @DisplayName("전체 게시판 게시글 검색(내용) 테스트")
  void searchByContents_success() throws Exception {
    given(postRepository.findByContents(eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByContents(keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByContents(eq(keyword), eq(pageable));
  }

  @Test
  @DisplayName("전체 게시판 게시글 검색(작성자) 테스트")
  void searchByWriter_success() throws Exception {
    given(postRepository.findByWriter(eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByWriter(keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByWriter(eq(keyword), eq(pageable));
  }

  @Test
  @DisplayName("전체 게시판 게시글 검색(댓글) 테스트")
  void searchByComment_success() throws Exception {
    given(postRepository.findByComment(eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByComment(keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByComment(eq(keyword), eq(pageable));
  }

  @Test
  @DisplayName("전체 게시판 게시판 게시글 조회 테스트")
  void getPage_withCategory_success() throws Exception {
    given(postRepository.findByCategory(eq(category), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByCategory(category, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByCategory(eq(category), eq(pageable));
  }

  @Test
  @DisplayName("게시글 검색(제목, 내용, 작성자) 테스트")
  void searchByAl_withCategory_success() throws Exception {
    given(postRepository.findByAllTarget(eq(category), eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByAllTarget(category, keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByAllTarget(eq(category), eq(keyword), eq(pageable));
  }

  @Test
  @DisplayName("게시글 검색(제목, 내용) 테스트")
  void searchByTitleAndContents_withCategory_success() throws Exception {
    given(postRepository.findByTitleAndContents(eq(category), eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByTitleAndContents(category, keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByTitleAndContents(eq(category), eq(keyword), eq(pageable));
  }

  @Test
  @DisplayName("게시글 검색(제목) 테스트")
  void searchByTitle_withCategory_success() throws Exception {
    given(postRepository.findByTitle(eq(category), eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByTitle(category, keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByTitle(eq(category), eq(keyword), eq(pageable));
  }

  @Test
  @DisplayName("게시글 검색(내용) 테스트")
  void searchByContents_withCategory_success() throws Exception {
    given(postRepository.findByContents(eq(category), eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByContents(category, keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByContents(eq(category), eq(keyword), eq(pageable));
  }

  @Test
  @DisplayName("게시글 검색(작성자) 테스트")
  void searchByWriter_withCategory_success() throws Exception {
    given(postRepository.findByWriter(eq(category), eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByWriter(category, keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByWriter(eq(category), eq(keyword), eq(pageable));
  }

  @Test
  @DisplayName("게시글 검색(댓글) 테스트")
  void searchByComment_withCategory_success() throws Exception {
    given(postRepository.findByComment(eq(category), eq(keyword), eq(pageable))).willReturn(mockPage);

    Page<Post> searchResult = postRepository.findByComment(category, keyword, pageable);

    assertEquals(mockPage, searchResult);

    verify(postRepository).findByComment(eq(category), eq(keyword), eq(pageable));
  }
}
