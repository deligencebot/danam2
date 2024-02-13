package com.delbot.danam.domain.comment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.delbot.danam.domain.comment.entity.Comment;
import com.delbot.danam.domain.comment.repository.CommentRepository;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.util.CustomTestUtils;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
  //
  @InjectMocks
  CommentService commentService;

  @Mock
  CommentRepository commentRepository;

  Member mockMember;
  Post mockPost;
  Comment mockComment;
  List<Comment> mockCommentList;
  
  @BeforeEach
  void setup() {
    mockMember = CustomTestUtils.createMockMember();
    mockPost = CustomTestUtils.createMockPost(mockMember);
    mockComment = CustomTestUtils.createMockComment(1L, mockMember, mockPost);
    mockCommentList = new ArrayList<>();

    for (long l = 1; l <= 10; l++) {
      mockCommentList.add(CustomTestUtils.createMockComment(l, mockMember, mockPost));
    }
  }

  @Test
  @DisplayName("댓글 조회 테스트")
  void getComments_success() throws Exception {
    given(commentRepository.findByPost(eq(mockPost))).willReturn(mockCommentList);

    List<Comment> commentList = commentRepository.findByPost(mockPost);

    assertEquals(mockCommentList, commentList);

    verify(commentRepository).findByPost(eq(mockPost));
  }

  @Test
  @DisplayName("댓글 저장 테스트")
  void saveComment_success() throws Exception {
    given(commentRepository.save(eq(mockComment))).willReturn(mockComment);

    Comment comment = commentRepository.save(mockComment);

    assertEquals(mockComment, comment);

    verify(commentRepository).save(eq(mockComment));
  }

  @Test
  @DisplayName("댓글 삭제 테스트 - Children 없음")
  void deleteComment_withoutChildren_success() throws Exception {
    doNothing().when(commentRepository).delete(eq(mockComment));

    commentService.deleteComment(mockComment);

    assertFalse(mockComment.isDeleted());

    verify(commentRepository).delete(eq(mockComment));
  }

  @Test
  @DisplayName("댓글 삭제 테스트 - Children 있음")
  void deleteComment_withChildren_success() throws Exception {
    Comment mockCommentParent = new Comment(
      2L, 
      "parent comment", 
      mockMember, 
      mockPost, 
      null, 
      0, 
      false, 
      false, 
      List.of(mockComment), 
      null, 
      null);

    commentService.deleteComment(mockCommentParent);

    assertTrue(mockCommentParent.isDeleted());

    verify(commentRepository, times(0)).delete(eq(mockCommentParent));
  }

  @Test
  @DisplayName("회원 댓글 불러오기 테스트")
  void getMemberInfoComments_success() throws Exception {
    given(commentRepository.getMemberInfoComments(mockMember)).willReturn(mockCommentList);

    List<Comment> memberCommentList = commentRepository.getMemberInfoComments(mockMember);

    assertEquals(mockCommentList, memberCommentList);

    verify(commentRepository).getMemberInfoComments(eq(mockMember));
  }
}
