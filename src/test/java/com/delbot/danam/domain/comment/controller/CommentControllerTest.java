package com.delbot.danam.domain.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.delbot.danam.config.TestSecurityConfig;
import com.delbot.danam.domain.comment.dto.CommentRequestDto;
import com.delbot.danam.domain.comment.entity.Comment;
import com.delbot.danam.domain.comment.service.CommentService;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.service.MemberService;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.service.PostService;
import com.delbot.danam.global.security.jwt.token.JwtAuthenticationToken;
import com.delbot.danam.global.security.jwt.util.IfLoginArgumentResolver;
import com.delbot.danam.global.security.jwt.util.LoginUserDto;
import com.delbot.danam.util.CustomTestUtils;

@WebMvcTest(controllers = CommentController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureWebMvc
public class CommentControllerTest {
  //
  @Autowired
  MockMvc mockMvc;

  @MockBean
  CommentService commentService;

  @MockBean
  MemberService memberService;

  @MockBean
  PostService postService;

  @MockBean
  IfLoginArgumentResolver ifLoginArgumentResolver;

  // http://localhost:8080/comments
  @Test
  @DisplayName("댓글 작성 테스트 - parent 없음")
  void writeComment_withoutParent() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    Post mockPost = CustomTestUtils.createMockPost(mockMember);
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);

    List<Comment> commentList = new ArrayList<>();
    for (long l = 1; l <= 10; l++) {
      commentList.add(CustomTestUtils.createMockComment(l, mockMember, mockPost));
    }

    CommentRequestDto.Post request = new CommentRequestDto.Post();
    request.setCategory(mockPost.getCategory());
    request.setPostNo(mockPost.getPostNo());
    request.setContents("new comment!!");

    int size = commentList.size();

    commentList.add(new Comment(
            Long.valueOf(size + 1), 
            request.getContents(), 
            mockMember, 
            mockPost, 
            null, 
            0, 
            false, 
            false, 
            null, 
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 
            null));

    given(memberService.findById(anyLong())).willReturn(mockMember);

    given(postService.getPost(anyString(), anyLong())).willReturn(mockPost);

    given(commentService.getComments(any(Post.class))).willReturn(commentList);

    mockMvc.perform(
            post("/comments")
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$[*].commentId").exists())
            .andExpect(jsonPath("$[*].contents").exists())
            .andExpect(jsonPath("$[*].depth").exists())
            .andExpect(jsonPath("$[*].createdTime").exists())
            .andExpect(jsonPath("$[" + size + "].contents").value(request.getContents()));
  }

  // http://localhost:8080/comments
  @Test
  @DisplayName("댓글 작성 테스트 - parent 있음")
  void writeComment_withParent() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    Post mockPost = CustomTestUtils.createMockPost(mockMember);
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);

    CommentRequestDto.Post request = new CommentRequestDto.Post();
    request.setCategory(mockPost.getCategory());
    request.setPostNo(mockPost.getPostNo());
    request.setContents("new comment!!");
    request.setParentId(1L);

    List<Comment> commentList = new ArrayList<>();

    Comment comment = new Comment(
            11L, 
            request.getContents(), 
            mockMember, 
            mockPost, 
            null, 
            0, 
            false, 
            false, 
            null, 
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 
            null);

    Comment parent = new Comment(
            1L, 
            "Fake Comment Contents No.1", 
            mockMember, 
            mockPost, 
            null, 
            0, 
            false, 
            false, 
            List.of(comment), 
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 
            null);
    comment.updateParent(parent);
  
    commentList.add(parent);
    for (long l = 2; l <= 10; l++) {
      commentList.add(CustomTestUtils.createMockComment(l, mockMember, mockPost));
    }

    comment.updateComment(request.getContents());
    commentList.add(comment);

    given(commentService.findById(anyLong())).willReturn(parent);

    given(memberService.findById(anyLong())).willReturn(mockMember);

    given(postService.getPost(anyString(), anyLong())).willReturn(mockPost);

    given(commentService.getComments(any(Post.class))).willReturn(commentList);

    mockMvc.perform(
            post("/comments")
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$[*].commentId").exists())
            .andExpect(jsonPath("$[*].contents").exists())
            .andExpect(jsonPath("$[*].depth").exists())
            .andExpect(jsonPath("$[*].createdTime").exists())
            .andExpect(jsonPath("$[0].children[0].contents").value(request.getContents()));
  }

  @Test
  @DisplayName("댓글 수정 테스트")
  void updateComment_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    Post mockPost = CustomTestUtils.createMockPost(mockMember);
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);
    LoginUserDto loginUserDto = CustomTestUtils.getLoginUserDto(mockMember);

    Comment mockComment = CustomTestUtils.createMockComment(1L, mockMember, mockPost);
    Comment updatedComment = mockComment;

    CommentRequestDto.Update request = new CommentRequestDto.Update();
    request.setCommentId(mockComment.getCommentId());
    request.setContents("comment update");

    updatedComment.updateComment(request.getContents());

    given(commentService.findById(anyLong())).willReturn(mockComment);

    given(commentService.getComments(any(Post.class))).willReturn(List.of(updatedComment));

    mockMvc.perform(
            put("/comments")
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .requestAttr("loginUserDto", loginUserDto)
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].commentId").exists())
            .andExpect(jsonPath("$[*].contents").exists())
            .andExpect(jsonPath("$[*].depth").exists())
            .andExpect(jsonPath("$[*].createdTime").exists())
            .andExpect(jsonPath("$[0].contents").value(request.getContents()));
  }

  @Test
  @DisplayName("댓글 삭제 테스트")
  void deleteComment_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    Post mockPost = CustomTestUtils.createMockPost(mockMember);
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);
    LoginUserDto loginUserDto = CustomTestUtils.getLoginUserDto(mockMember);

    Comment mockComment = CustomTestUtils.createMockComment(1L, mockMember, mockPost);

    CommentRequestDto.Delete request = new CommentRequestDto.Delete();
    request.setCommentId(1L);

    List<Comment> commentList = new ArrayList<>();
    for (long l = 2; l <= 10; l++) {
      commentList.add(CustomTestUtils.createMockComment(l, mockMember, mockPost));
    }

    given(commentService.findById(anyLong())).willReturn(mockComment);

    doNothing().when(commentService).deleteComment(any());

    given(commentService.getComments(any(Post.class))).willReturn(commentList);

    mockMvc.perform(
            delete("/comments")
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .requestAttr("loginUserDto", loginUserDto)
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(jsonPath("$[*].commentId[?(@ != 1)]").doesNotExist());
  }
}
