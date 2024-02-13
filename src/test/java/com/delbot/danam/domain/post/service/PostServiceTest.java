package com.delbot.danam.domain.post.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

  Post mockPost;

  @BeforeEach
  void setup() {
    mockPost = CustomTestUtils.createMockPost(mock(Member.class));
  }

  @Test
  @DisplayName("게시글 조회 테스트")
  void getPost_success() throws Exception {
    given(postRepository.findByCategoryAndPostNo(eq(mockPost.getCategory()), eq(mockPost.getPostNo())))
            .willReturn(Optional.of(mockPost));
    
    Post post = postRepository.findByCategoryAndPostNo(mockPost.getCategory(), mockPost.getPostNo()).get();

    assertEquals(mockPost.getPostId(), post.getPostId());

    verify(postRepository).findByCategoryAndPostNo(anyString(), anyLong());
  }

  @Test
  @DisplayName("게시글 등록 테스트")
  void addPost_success() throws Exception {
    given(postRepository.save(eq(mockPost))).willReturn(mockPost);

    Post post = postRepository.save(mockPost);

    assertEquals(mockPost, post);

    verify(postRepository).save(eq(mockPost));
  }

  @Test
  @DisplayName("게시글 삭제 테스트")
  void deletePost_success() throws Exception {
    doNothing().when(postRepository).delete(any(Post.class));

    postRepository.delete(mockPost);

    verify(postRepository).delete(any(Post.class));
  }

  @Test
  @DisplayName("게시글 조회수 테스트")
  void updateHits_success() throws Exception {
    doNothing().when(postRepository).updateHits(anyLong());

    postRepository.updateHits(mockPost.getPostId());

    verify(postRepository).updateHits(anyLong());
  }

  @Test
  @DisplayName("게시글 번호 초기화 테스트")
  void initPostNo_success() throws Exception {
    String category = "fakeBoard";

    given(postRepository.findByCategory(eq(category)))
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

    verify(postRepository).findByCategory(eq(category));
  }

  @Test
  @DisplayName("회원게시글 불러오기 테스트")
  void getMemberInfoPosts_success() throws Exception {
    given(postRepository.getMemberInfoPosts(any(Member.class))).willReturn(Arrays.asList(
            new Post(1L, "category", "title1", "hello", mock(Member.class)),
            new Post(2L, "category", "title1", "hello", mock(Member.class)),
            new Post(3L, "category", "title1", "hello", mock(Member.class))
    ));

    postRepository.getMemberInfoPosts(mock(Member.class));

    verify(postRepository).getMemberInfoPosts(any(Member.class));
  }
}
