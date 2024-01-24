package com.delbot.danam.domain.post.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.service.MemberService;
import com.delbot.danam.domain.post.dto.PostRequestDto;
import com.delbot.danam.domain.post.dto.PostResponseDto;
import com.delbot.danam.domain.post.dto.PostUpdateRequestDto;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.entity.PostFile;
import com.delbot.danam.domain.post.entity.PostImage;
import com.delbot.danam.domain.post.exception.PostErrorCode;
import com.delbot.danam.domain.post.repository.PostFileRepository;
import com.delbot.danam.domain.post.repository.PostImageRepository;
import com.delbot.danam.domain.post.service.PageService;
import com.delbot.danam.domain.post.service.PostService;
import com.delbot.danam.global.common.aws.AwsFileInfo;
import com.delbot.danam.global.common.aws.AwsS3Service;
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
  private final AwsS3Service awsS3Service;
  private final PostImageRepository postImageRepository;
  private final PostFileRepository postFileRepository;

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
      return PostResponseDto.Pages.builder()
              .postId(post.getPostId())
              .postNo(post.getPostNo())
              .category(post.getCategory())
              .title(post.getTitle())
              .writer(post.getMember().getNickname())
              .hits(post.getHits())
              .createdTime(post.getCreatedTime())
              .updatedTime(post.getUpdatedTime())
              .build();
    });
    return new ResponseEntity<>(pageResponse, HttpStatus.OK);
  }

  @GetMapping("/{category}/{no}")
  public ResponseEntity<?> viewPost(@PathVariable String category, @PathVariable Long no) {
    Post post = postService.getPost(category, no);
    postService.updateHits(post);

    PostResponseDto.Detail postResponse = PostResponseDto.Detail.builder().post(post).build();
    return new ResponseEntity<>(postResponse, HttpStatus.OK);
  }

  @Transactional
  @PutMapping("/{category}/{no}")
  public ResponseEntity<?> updatePost(
          @PathVariable String category, 
          @PathVariable Long no, @Valid 
          @RequestPart(value = "request") PostUpdateRequestDto request, BindingResult bindingResult, 
          @RequestPart(required = false, value = "files") List<MultipartFile> files,
          @RequestPart(required = false, value = "images") List<MultipartFile> images,
          @IfLogin LoginUserDto loginUserDto) {
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

    /* File수정 처리시작 */ 
    if (!CollectionUtils.isEmpty(request.getDeleteFileUrls())) {
      request.getDeleteFileUrls().forEach(deleteFileUrl -> {
        PostFile deletePostFile = postFileRepository.findByFileUrl(deleteFileUrl);
        awsS3Service.deleteFile(deletePostFile.getStoredFileName());
        postFileRepository.delete(deletePostFile);
      });
    }

    if (!CollectionUtils.isEmpty(files)) {
      files.forEach(file -> {
        try {
          AwsFileInfo fileInfo = awsS3Service.uploadFile(file);
          postFileRepository.save(new PostFile(fileInfo.getFileUrl(), fileInfo.getStoredFileName(), file.getOriginalFilename(), file.getSize(), updatedPost));
        } catch (IOException e) {
          throw PostErrorCode.FILE_UPLOAD_ERROR.defaultException();
        }
      });
    }
    /* File수정 처리완료 */ 

    /* Image수정 처리시작 */ 
    if (!CollectionUtils.isEmpty(request.getDeleteImageUrls())) {
      request.getDeleteImageUrls().forEach(deleteImageUrl -> {
        PostImage deletePostImage = postImageRepository.findByImageUrl(deleteImageUrl);
        awsS3Service.deleteFile(deletePostImage.getStoredFileName());
        postImageRepository.delete(deletePostImage);
      });
    }

    if (!CollectionUtils.isEmpty(images)) {
      images.forEach(image -> {
        try {
          AwsFileInfo fileInfo = awsS3Service.uploadFile(image);
          postImageRepository.save(new PostImage(fileInfo.getFileUrl(), fileInfo.getStoredFileName(), image.getOriginalFilename(), image.getSize(), updatedPost));
        } catch (IOException e) {
          throw PostErrorCode.FILE_UPLOAD_ERROR.defaultException();
        }
      });
    } 
    /* Image수정 처리완료 */ 

    // 갱신된 Post 호출
    Post responsePost = postService.getPost(updatedPost.getCategory(), updatedPost.getPostNo());

    PostResponseDto.Detail postResponse = PostResponseDto.Detail.builder().post(responsePost).build();
    return new ResponseEntity<>(postResponse, HttpStatus.OK);
  }

  @Transactional
  @PostMapping("/{category}/posting")
  public ResponseEntity<?> posting(
          @PathVariable String category, 
          @Valid @RequestPart(value = "request") PostRequestDto request, BindingResult bindingResult,
          @RequestPart(required = false, value = "files") List<MultipartFile> files,
          @RequestPart(required = false, value = "images") List<MultipartFile> images,
          @IfLogin LoginUserDto loginUserDto) {
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

    if (!CollectionUtils.isEmpty(files)) {
      files.forEach(file -> {
        try {
          AwsFileInfo fileInfo = awsS3Service.uploadFile(file);
          postFileRepository.save(new PostFile(fileInfo.getFileUrl(), fileInfo.getStoredFileName(), file.getOriginalFilename(), file.getSize(), savedPost));
        } catch (IOException e) {
          throw PostErrorCode.FILE_UPLOAD_ERROR.defaultException();
        }
      });
    }

    if (!CollectionUtils.isEmpty(images)) {
      images.forEach(image -> {
        try {
          AwsFileInfo fileInfo = awsS3Service.uploadFile(image);
          postImageRepository.save(new PostImage(fileInfo.getFileUrl(), fileInfo.getStoredFileName(), image.getOriginalFilename(), image.getSize(), savedPost));
        } catch (IOException e) {
          throw PostErrorCode.FILE_UPLOAD_ERROR.defaultException();
        }
      });
    } 

    // 갱신된 Post 호출
    Post responsePost = postService.getPost(savedPost.getCategory(), savedPost.getPostNo());

    PostResponseDto.Detail postResponse = PostResponseDto.Detail.builder().post(responsePost).build();
    return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
  }

  @Transactional
  @DeleteMapping("/{category}/{no}")
  public ResponseEntity<?> deletePost(@PathVariable String category, @PathVariable Long no, @IfLogin LoginUserDto loginUserDto) {
    Member member = memberService.findById(loginUserDto.getMemberId());
    Post post = postService.getPost(category, no);
    
    if (!member.equals(post.getMember())) {
      throw PostErrorCode.UNAUTHORIZED_ACCESS.defaultException();
    }
    
    post.getPostImages().forEach(postImage -> {
      awsS3Service.deleteFile(postImage.getStoredFileName());
    });

    postService.deletePost(post);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
