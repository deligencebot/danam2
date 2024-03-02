package com.delbot.danam.domain.comment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delbot.danam.domain.category.Category;
import com.delbot.danam.domain.category.CategoryService;
import com.delbot.danam.domain.comment.dto.CommentRequestDto;
import com.delbot.danam.domain.comment.dto.CommentResponseDto;
import com.delbot.danam.domain.comment.entity.Comment;
import com.delbot.danam.domain.comment.exception.CommentErrorCode;
import com.delbot.danam.domain.comment.service.CommentService;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.service.MemberService;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.service.PostService;
import com.delbot.danam.global.security.jwt.util.IfLogin;
import com.delbot.danam.global.security.jwt.util.LoginUserDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
  //
  private final CommentService commentService;
  private final MemberService memberService;
  private final PostService postService;
  private final CategoryService categoryService;

  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @PostMapping
  public ResponseEntity<?> writeComments(@Valid @RequestBody CommentRequestDto.Post request, BindingResult bindingResult, @IfLogin LoginUserDto loginUserDto) {
    if (bindingResult.hasErrors()) {
      throw CommentErrorCode.INVALID_INPUT_VALUE.defaultException();
    }

    Member member = memberService.findById(loginUserDto.getMemberId());
    Category category = categoryService.findByName(request.getCategory());
    Post post = postService.getPost(category, request.getPostNo());
    int depth = 0;
    Comment parent = null;
    if (request.getParentId() != null) {
      parent = commentService.findById(request.getParentId());
      depth = parent.getDepth() + 1;
    }

    Comment comment = Comment.builder()
            .contents(request.getContents())
            .member(member)
            .post(post)
            .depth(depth)
            .parent(parent)
            .build();

    commentService.saveComment(comment);

    List<CommentResponseDto> response = CommentResponseDto.listMapper(commentService.getComments(post));

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @PutMapping
  public ResponseEntity<?> updateComment(@Valid @RequestBody CommentRequestDto.Update request, BindingResult bindingResult, @IfLogin LoginUserDto loginUserDto) {
    if (bindingResult.hasErrors()) {
      throw CommentErrorCode.INVALID_INPUT_VALUE.defaultException();
    }

    Comment comment = commentService.findById(request.getCommentId());

    if (!comment.getMember().getMemberId().equals(loginUserDto.getMemberId())) {
      throw CommentErrorCode.UNAUTHORIZED_ACCESS.defaultException();
    }

    comment.updateComment(request.getContents());

    commentService.saveComment(comment);

    List<CommentResponseDto> response = CommentResponseDto.listMapper(commentService.getComments(comment.getPost()));

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @DeleteMapping
  public ResponseEntity<?> deleteComment(@RequestBody CommentRequestDto.Delete request, @IfLogin LoginUserDto loginUserDto) {
    Comment comment = commentService.findById(request.getCommentId());

    if (!(loginUserDto.getMemberId().equals(comment.getMember().getMemberId()) || loginUserDto.getRoles().contains("ROLE_ADMIN"))) {
      throw CommentErrorCode.UNAUTHORIZED_ACCESS.defaultException();
    }

    commentService.deleteComment(comment);

    List<CommentResponseDto> response = CommentResponseDto.listMapper(commentService.getComments(comment.getPost()));

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
