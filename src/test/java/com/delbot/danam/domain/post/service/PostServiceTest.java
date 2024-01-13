package com.delbot.danam.domain.post.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.Arrays;
import java.util.Comparator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.repository.PostRepository;
import com.delbot.danam.util.CustomTestUtils;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
  //
  @InjectMocks
  PostService postService;

  @Mock
  PostRepository postRepository;

  @Test
  @DisplayName("게시글 조회 테스트")
  void getPost_success() throws Exception {
    Post mockPost = CustomTestUtils.createMockPost(mock(Member.class));
    String category = "board";
    Long postNo = 1L;

    given(postRepository.findByCategoryAndPostNo(anyString(), anyLong()))
            .willReturn(Optional.of(mockPost));
    
    Post post = postRepository.findByCategoryAndPostNo(category, postNo).get();

    assertEquals(category, post.getCategory());
    assertEquals(postNo, post.getPostNo());

    verify(postRepository).findByCategoryAndPostNo(anyString(), anyLong());
  }

  @Test
  @DisplayName("게시글 등록 테스트")
  void addPost_success() throws Exception {
    Post mockPost = CustomTestUtils.createMockPost(mock(Member.class));

    given(postRepository.save(any(Post.class))).willReturn(mockPost);

    Post post = postRepository.save(mockPost);

    assertEquals(mockPost, post);

    verify(postRepository).save(any(Post.class));
  }

  @Test
  @DisplayName("게시글 삭제 테스트")
  void deletePost_success() throws Exception {
    Post mockPost = CustomTestUtils.createMockPost(mock(Member.class));

    doNothing().when(postRepository).delete(any(Post.class));

    postRepository.delete(mockPost);

    verify(postRepository).delete(any(Post.class));
  }

  @Test
  @DisplayName("게시글 조회수 테스트")
  void updateHits_success() throws Exception {
    Long mockPostId = 1L;

    doNothing().when(postRepository).updateHits(anyLong());

    postRepository.updateHits(mockPostId);

    verify(postRepository).updateHits(anyLong());
  }

  @Test
  @DisplayName("게시글 번호 초기화 테스트")
  void initPostNo_success() throws Exception {
    String category = "board";

    given(postRepository.findByCategory(anyString()))
            .willReturn(Arrays.asList(
              new Post(1L, category, "title1", "hello", mock(Member.class)),
              new Post(2L, category, "title1", "hello", mock(Member.class)),
              new Post(3L, category, "title1", "hello", mock(Member.class))
            ));
    
    Long result = postRepository.findByCategory(category)
            .stream()
            .map(Post::getPostNo)
            .max(Comparator.naturalOrder())
            .orElse(0L) + 1L;

    assertEquals(4L, result);

    verify(postRepository).findByCategory(anyString());
  }
}
