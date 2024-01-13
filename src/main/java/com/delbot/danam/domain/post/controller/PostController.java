package com.delbot.danam.domain.post.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.service.MemberService;
import com.delbot.danam.domain.post.dto.PostRequestDto;
import com.delbot.danam.domain.post.dto.PostResponseDto;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.exception.PostErrorCode;
import com.delbot.danam.domain.post.service.PageService;
import com.delbot.danam.domain.post.service.PostService;
import com.delbot.danam.global.security.jwt.util.IfLogin;
import com.delbot.danam.global.security.jwt.util.LoginUserDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
  //
  private final PostService postService;
  private final PageService pageService;
  private final MemberService memberService;

  @GetMapping("/{category}")
  public ResponseEntity<?> category(
        @PathVariable String category, 
        @RequestParam(required = false) String target, 
        @RequestParam(required = false) String keyword, 
        @PageableDefault(page = 1) Pageable pageable) {
    Page<Post> postList = null;
    
    if (target != null && keyword != null) {
      if (target.equals("all")) {
        postList = pageService.searchByAll(category, keyword, pageable);
      } else if(target.equals("title_contents")) {
        postList = pageService.searchByTitleAndContents(category, keyword, pageable);
      } else if(target.equals("title")) {
        postList = pageService.searchByTitle(category, keyword, pageable);
      } else if(target.equals("contents")) {
        postList = pageService.searchByContents(category, keyword, pageable);
      } else if(target.equals("writer")) {
        postList = pageService.searchByWriter(category, keyword, pageable);
      } else if(target.equals("comment")) {
      // TODO : Find Posts By Comment
      } 
    } else {
      postList = pageService.getPage(category, pageable);
    }

    Page<PostResponseDto.Pages> pageResponse = postList.map(post -> {
      return new PostResponseDto.Pages(
        post.getPostId(),
        post.getPostNo(),
        post.getCategory(),
        post.getTitle(),
        post.getMember().getNickname(),
        post.getHits(),
        post.getCreatedTime(),
        post.getUpdatedTime()
      );
    });
    return new ResponseEntity<>(pageResponse, HttpStatus.OK);
  }

  @GetMapping("/{category}/{no}")
  public ResponseEntity<?> viewPost(@PathVariable String category, @PathVariable Long no) {
    Post post = postService.getPost(category, no);
    postService.updateHits(post);

    PostResponseDto.Detail postResponse = PostResponseDto.Detail.builder()
            .postId(post.getPostId())
            .postNo(post.getPostNo())
            .category(post.getCategory())
            .title(post.getTitle())
            .contents(post.getContents())
            .writer(post.getMember().getNickname())
            .hits(post.getHits())
            .createdTime(post.getCreatedTime())
            .updatedTime(post.getUpdatedTime())
            .build();
    return new ResponseEntity<>(postResponse, HttpStatus.OK);
  }

  @PutMapping("/{category}/{no}")
  public ResponseEntity<?> updatePost(@PathVariable String category, @PathVariable Long no, @Valid @RequestBody PostRequestDto request, BindingResult bindingResult, @IfLogin LoginUserDto loginUserDto) {
    if (bindingResult.hasErrors()) {
      throw PostErrorCode.INVALID_VALUE.defaultException();
    }

    Member member = memberService.findById(loginUserDto.getMemberId());
    Post post = postService.getPost(category, no);

    if (!member.equals(post.getMember())) {
      throw PostErrorCode.UNAUTHORIZED_ACCESS.defaultException();
    }

    post.update(request.getTitle(), request.getContents());
    Post updatedPost = postService.addPost(post);
    PostResponseDto.Detail postResponse = PostResponseDto.Detail.builder()
            .postId(updatedPost.getPostId())
            .postNo(updatedPost.getPostNo())
            .category(updatedPost.getCategory())
            .title(updatedPost.getTitle())
            .contents(updatedPost.getContents())
            .writer(updatedPost.getMember().getNickname())
            .hits(updatedPost.getHits())
            .createdTime(updatedPost.getCreatedTime())
            .updatedTime(updatedPost.getUpdatedTime())
            .build();
    return new ResponseEntity<>(postResponse, HttpStatus.OK);
  }

  @PostMapping("/{category}/posting")
  public ResponseEntity<?> posting(@PathVariable String category, @Valid @RequestBody PostRequestDto request, BindingResult bindingResult, @IfLogin LoginUserDto loginUserDto) {
    if (bindingResult.hasErrors()) {
      throw PostErrorCode.INVALID_VALUE.defaultException();
    }
    Member member = memberService.findById(loginUserDto.getMemberId());
    Post post = Post.builder()
            .postNo(postService.initPostNo(category))
            .category(category)
            .title(request.getTitle())
            .contents(request.getContents())
            .member(member)
            .build();
    Post savedPost = postService.addPost(post);
    PostResponseDto.Detail postResponse = PostResponseDto.Detail.builder()
            .postId(savedPost.getPostId())
            .postNo(savedPost.getPostNo())
            .category(savedPost.getCategory())
            .title(savedPost.getTitle())
            .contents(savedPost.getContents())
            .writer(savedPost.getMember().getNickname())
            .hits(savedPost.getHits())
            .createdTime(savedPost.getCreatedTime())
            .updatedTime(savedPost.getUpdatedTime())
            .build();
    return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
  }

  @DeleteMapping("/{category}/{no}")
  public ResponseEntity<?> deletePost(@PathVariable String category, @PathVariable Long no, @IfLogin LoginUserDto loginUserDto) {
    Member member = memberService.findById(loginUserDto.getMemberId());
    Post post = postService.getPost(category, no);
    
    if (!member.equals(post.getMember())) {
      throw PostErrorCode.UNAUTHORIZED_ACCESS.defaultException();
    }

    postService.deletePost(post);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
