package com.delbot.danam.domain.post.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;

import com.delbot.danam.config.TestSecurityConfig;
import com.delbot.danam.domain.category.Category;
import com.delbot.danam.domain.category.CategoryService;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.service.MemberService;
import com.delbot.danam.domain.post.dto.PostRequestDto;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.entity.PostFile;
import com.delbot.danam.domain.post.entity.PostImage;
import com.delbot.danam.domain.post.repository.PostFileRepository;
import com.delbot.danam.domain.post.repository.PostImageRepository;
import com.delbot.danam.domain.post.service.PageService;
import com.delbot.danam.domain.post.service.PostService;
import com.delbot.danam.domain.role.Role;
import com.delbot.danam.global.common.aws.AwsFileInfo;
import com.delbot.danam.global.common.aws.AwsS3Service;
import com.delbot.danam.global.security.jwt.token.JwtAuthenticationToken;
import com.delbot.danam.global.security.jwt.util.IfLoginArgumentResolver;
import com.delbot.danam.util.CustomTestUtils;

@WebMvcTest(controllers = PostController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureWebMvc 
public class PostControllerTest {
  //
  @Autowired
  MockMvc mockMvc;

  @MockBean
  PostService postService;

  @MockBean
  PageService pageService;

  @MockBean
  MemberService memberService;

  @MockBean
  CategoryService categoryService;

  @MockBean
  AwsS3Service awsS3Service;

  @MockBean
  PostFileRepository postFileRepository;

  @MockBean
  PostImageRepository postImageRepository;

  @MockBean
  IfLoginArgumentResolver ifLoginArgumentResolver;

  // http://localhost:8080/post
  @Test
  @DisplayName("모든 게시판 게시글 검색(전체) 테스트")
  void searchPosts_success() throws Exception {
    Page<Post> mockPage = CustomTestUtils.generateMockPage("mockCategory", PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    Authentication authentication = new TestingAuthenticationToken("user0001", null, "USER");

    given(pageService.getPage(any(Pageable.class))).willReturn(mockPage);

    mockMvc.perform(
            get("/post")
            .with(authentication(authentication)))            
            .andDo(print())
            .andExpect(status().isOk());

    verify(pageService).getPage(any(Pageable.class));
  }

  // http://localhost:8080/post?target=comment&keyword=aaa
  @Test
  @DisplayName("모든 게시판 게시글 검색(댓글) 테스트")
  void searchPosts_comment_success() throws Exception {
    Page<Post> mockPage = CustomTestUtils.generateMockPage("mockCategory", PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    Authentication authentication = new TestingAuthenticationToken("user0001", null, "USER");

    given(pageService.searchByComment(anyString(), any(Pageable.class))).willReturn(mockPage);

    mockMvc.perform(
            get("/post")
            .param("target", "comment")
            .param("keyword", "aaa")
            .with(authentication(authentication)))            
            .andDo(print())
            .andExpect(status().isOk());

    verify(pageService).searchByComment(anyString(), any(Pageable.class));
  }

  // http://localhost:8080/post/{category} (category = board)
  @Test
  @DisplayName("게시글 검색(전체) 테스트")
  void searchCategoryPosts_success() throws Exception {
    String category = "board";
    Category postCategory = CustomTestUtils.categoryMockCategory(category);
    Page<Post> mockPage = CustomTestUtils.generateMockPage(category, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    Authentication authentication = new TestingAuthenticationToken("user0001", null, "USER");

    given(categoryService.findByName(anyString())).willReturn(postCategory);

    given(pageService.getPage(any(Category.class), any(Pageable.class))).willReturn(mockPage);

    mockMvc.perform(
            get("/post/{category}", category)
            .with(authentication(authentication)))            
            .andDo(print())
            .andExpect(status().isOk());

    verify(pageService).getPage(any(Category.class), any(Pageable.class));
  }

  // http://localhost:8080/post/{category}?target=all&keyword=aaa (category = board)
  @Test
  @DisplayName("게시글 검색(모든조건) 테스트")
  void searchCategoryPosts_All_success() throws Exception {
    String category = "board";
    Category postCategory = CustomTestUtils.categoryMockCategory(category);
    Page<Post> mockPage = CustomTestUtils.generateMockPage(category, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    Authentication authentication = new TestingAuthenticationToken("user0001", null, "USER");

    given(categoryService.findByName(anyString())).willReturn(postCategory);

    given(pageService.searchByAll(any(Category.class), anyString(), any(Pageable.class))).willReturn(mockPage);

    mockMvc.perform(
            get("/post/{category}", category)
            .param("target", "all")
            .param("keyword", "aaa")
            .with(authentication(authentication)))            
            .andDo(print())
            .andExpect(status().isOk());

    verify(pageService).searchByAll(any(Category.class), anyString(), any(Pageable.class));
  }

  // http://localhost:8080/post/{category}/{no} (category = board, no = 1)
  @Test
  @DisplayName("게시글 조회 테스트")
  void viewPost_success() throws Exception {
    String category = "board";
    Category postCategory = CustomTestUtils.categoryMockCategory(category);
    Long no = 1L;
    Authentication authentication = new TestingAuthenticationToken("user0001", null, "USER");

    Post mockPost = CustomTestUtils.createMockPost(new Member("user0001", "password", "홍길동", "user0001@gmail.com"));

    given(categoryService.findByName(anyString())).willReturn(postCategory);

    given(postService.getPost(any(Category.class), anyLong())).willReturn(mockPost);

    mockMvc.perform(
            get("/post/{category}/{no}", category, no)
            .with(authentication(authentication)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.postId").exists())
            .andExpect(jsonPath("$.postNo").exists())
            .andExpect(jsonPath("$.category").exists())
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.contents").exists())
            .andExpect(jsonPath("$.writer").exists())
            .andExpect(jsonPath("$.hits").exists())
            .andExpect(jsonPath("$.createdTime").exists());

    verify(postService).getPost(any(Category.class), anyLong());
  }

  // http://localhost:8080/post/{category}/{no} (category = board, no = 1)
  @Test
  @DisplayName("게시글 조회 테스트")
  void updatePost_success() throws Exception {
    String category = "board";
    Category postCategory = CustomTestUtils.categoryMockCategory(category);
    Long no = 1L;
    Authentication authentication = new TestingAuthenticationToken("user0001", null, "USER");

    Member mockMember = CustomTestUtils.createMockMember();
    Post mockPost = CustomTestUtils.createMockPost(mockMember);

    given(categoryService.findByName(anyString())).willReturn(postCategory);

    given(postService.getPost(any(Category.class), anyLong())).willReturn(mockPost);

    doNothing().when(postService).updateHits(any(Post.class));

    mockMvc.perform(
            get("/post/{category}/{no}", category, no)
            .with(authentication(authentication)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.postId").exists())
            .andExpect(jsonPath("$.postNo").exists())
            .andExpect(jsonPath("$.category").exists())
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.contents").exists())
            .andExpect(jsonPath("$.writer").exists())
            .andExpect(jsonPath("$.hits").exists())
            .andExpect(jsonPath("$.createdTime").exists());

    verify(postService).updateHits(any(Post.class));
  }

  // http://localhost:8080/post/{category}/{no} (category = board, no = 1)
  @Test
  @DisplayName("게시글 수정 테스트")
  void updatedPost_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    Post mockPost = CustomTestUtils.createMockPost(mockMember);
    String category = "board";
    Category postCategory = CustomTestUtils.categoryMockCategory(category);

    MockMultipartFile multipartFile3 = new MockMultipartFile("images", "image3.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes(StandardCharsets.UTF_8));

    PostImage mockPostImage1 = CustomTestUtils.createMockPostImage(mockPost, 1L);
    PostImage mockPostImage2 = CustomTestUtils.createMockPostImage(mockPost, 2L);
    PostImage mockPostImage3 = CustomTestUtils.createMockPostImage(mockPost, 3L);

    AwsFileInfo fileInfo3 = new AwsFileInfo(mockPostImage3.getImageUrl(), mockPostImage3.getStoredFileName());

    Long no = 1L;
    PostRequestDto.Update updateRequestDto = new PostRequestDto.Update();
    updateRequestDto.setTitle("updated title");
    updateRequestDto.setContents("updated Hello world!!");
    updateRequestDto.setNotice(false);
    updateRequestDto.setCommentable(true);
    updateRequestDto.setDeleteImageUrls(List.of(mockPostImage2.getImageUrl()));

    String request = CustomTestUtils.toJson(updateRequestDto);

    Post mockResponsePost = new Post(
            mockPost.getPostId(),  
            mockPost.getPostNo(), 
            mockPost.getCategory(), 
            updateRequestDto.getTitle(), 
            updateRequestDto.getContents(), 
            mockPost.getHits(), 
            mockPost.isNotice(), 
            mockPost.isUpdated(), 
            mockPost.isCommentable(), 
            mockPost.getCreatedTime(), 
            mockPost.getCreatedTime().plus(Duration.ofMillis(60 * 60 * 1000L)), 
            mockMember, 
            new ArrayList<>(),
            List.of(mockPostImage1, mockPostImage3),
            new ArrayList<>()
    );

    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);

    given(memberService.findById(anyLong())).willReturn(mockMember);

    given(categoryService.findByName(anyString())).willReturn(postCategory);

    given(postService.getPost(any(Category.class), anyLong())).willReturn(mockPost);

    given(postService.addPost(any())).willReturn(mockPost);

    given(postImageRepository.findByImageUrl(anyString())).willReturn(mockPostImage2);

    given(awsS3Service.uploadFile(any(MultipartFile.class))).willReturn(fileInfo3);

    given(postService.getPost(any(Category.class), anyLong())).willReturn(mockResponsePost);

    MockMultipartHttpServletRequestBuilder builder = 
            MockMvcRequestBuilders.multipart("/post/{category}/{no}", category, no);
    builder.with(new RequestPostProcessor() {
      @Override
      public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        request.setMethod("PUT");
        return request;
      }
    });

    mockMvc.perform(
            builder
            .file(new MockMultipartFile("request", "", "application/json", request.getBytes(StandardCharsets.UTF_8)))
            .file(multipartFile3)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.postId").exists())
            .andExpect(jsonPath("$.postNo").exists())
            .andExpect(jsonPath("$.category").exists())
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.contents").exists())
            .andExpect(jsonPath("$.writer").exists())
            .andExpect(jsonPath("$.postImages").exists())
            .andExpect(jsonPath("$.hits").exists())
            .andExpect(jsonPath("$.createdTime").exists())
            .andExpect(jsonPath("$.updatedTime").exists());

    assertEquals(updateRequestDto.getTitle(), mockResponsePost.getTitle());
    assertEquals(updateRequestDto.getContents(), mockResponsePost.getContents());
    
    verify(postService).addPost(any());
  }

  // http://localhost:8080/post/{category}/posting (category = board)
  @Test
  @DisplayName("게시글 작성 테스트")
  void posting_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);
    Post mockPost = CustomTestUtils.createMockPost(mockMember);

    PostFile mockPostFile = CustomTestUtils.createMockPostFile(mockPost, 1L);
    PostImage mockPostImage1 = CustomTestUtils.createMockPostImage(mockPost, 1L);
    PostImage mockPostImage2 = CustomTestUtils.createMockPostImage(mockPost, 2L);

    MockMultipartFile multipartFile = new MockMultipartFile("files", "file.txt", MediaType.TEXT_PLAIN_VALUE, "image".getBytes(StandardCharsets.UTF_8));
    MockMultipartFile multipartFile1 = new MockMultipartFile("images", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes(StandardCharsets.UTF_8));
    MockMultipartFile multipartFile2 = new MockMultipartFile("images", "image2.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes(StandardCharsets.UTF_8));

    AwsFileInfo fileInfo = new AwsFileInfo(mockPostFile.getFileUrl(), mockPostFile.getStoredFileName());
    AwsFileInfo fileInfo1 = new AwsFileInfo(mockPostImage1.getImageUrl(), mockPostImage1.getStoredFileName());
    AwsFileInfo fileInfo2 = new AwsFileInfo(mockPostImage2.getImageUrl(), mockPostImage2.getStoredFileName());

    Post mockResponsePost = new Post(
              mockPost.getPostId(),  
              mockPost.getPostNo(), 
              mockPost.getCategory(), 
              mockPost.getTitle(), 
              mockPost.getContents(), 
              mockPost.getHits(), 
              mockPost.isNotice(), 
              mockPost.isUpdated(), 
              mockPost.isCommentable(), 
              mockPost.getCreatedTime(), 
              null, 
              mockMember,
              new ArrayList<>(), 
              List.of(mockPostImage1, mockPostImage2),
              List.of(mockPostFile)
    );

    String category = "board";
    PostRequestDto.Post requestDto = new PostRequestDto.Post();
    requestDto.setTitle("Title");
    requestDto.setContents("Hello World!!");
    requestDto.setCommentable(true);
    requestDto.setNotice(false);

    String request = CustomTestUtils.toJson(requestDto);

    given(memberService.findById(anyLong())).willReturn(mockMember);

    given(postService.addPost(any(Post.class))).willReturn(mockPost);

    given(awsS3Service.uploadFile(multipartFile)).willReturn(fileInfo);

    given(postFileRepository.save(mockPostFile)).willReturn(mockPostFile);

    given(awsS3Service.uploadFile(multipartFile1)).willReturn(fileInfo1);
    given(awsS3Service.uploadFile(multipartFile2)).willReturn(fileInfo2);

    given(postImageRepository.save(mockPostImage1)).willReturn(mockPostImage1);
    given(postImageRepository.save(mockPostImage2)).willReturn(mockPostImage2);

    given(postService.getPost(any(Category.class), anyLong())).willReturn(mockResponsePost);

    mockMvc.perform(
            multipart("/post/{category}/posting", category)
            .file(new MockMultipartFile("request", "", "application/json", request.getBytes(StandardCharsets.UTF_8)))
            .file(multipartFile)
            .file(multipartFile1)
            .file(multipartFile2)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.postId").exists())
            .andExpect(jsonPath("$.postNo").exists())
            .andExpect(jsonPath("$.category").exists())
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.contents").exists())
            .andExpect(jsonPath("$.writer").exists())
            .andExpect(jsonPath("$.hits").exists())
            .andExpect(jsonPath("$.postImages").exists())
            .andExpect(jsonPath("$.postFiles").exists())
            .andExpect(jsonPath("$.createdTime").exists());

    verify(postService).addPost(any(Post.class));
  }

  // http://localhost:8080/post/{category}/{no} (category = board, no = 1)
  @Test
  @DisplayName("게시글 삭제 테스트")
  void deletePost_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);
    Post mockPost = CustomTestUtils.createMockPost(mockMember);

    given(memberService.findById(anyLong())).willReturn(mockMember);

    given(categoryService.findByName(anyString())).willReturn(mockPost.getCategory());

    given(postService.getPost(any(Category.class), anyLong())).willReturn(mockPost);

    mockMvc.perform(
            delete("/post/{category}/{no}", mockPost.getCategory().getName(), mockPost.getPostNo())
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk()); 

    verify(postService).getPost(any(Category.class), anyLong());
  }

  // http://localhost:8080/post/{category}/{no} (category = board, no = 1)
  @Test
  @DisplayName("게시글 삭제 테스트 - admin")
  void deletePost_admin_success() throws Exception {
    Member mockAdmin = CustomTestUtils.createMockMember();
    Role adminRole = new Role(2L, "ROLE_ADMIN");
    mockAdmin.addRole(adminRole);
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockAdmin);

    Role role = new Role(1L, "ROLE_USER");
    Set<Role> roles = Set.of(role);

    Member mockMember = new Member(
            2L,
            "mockUser",
            "asdf1234!",
            "MockUser",
            "mockuser@google.com",
            LocalDateTime.now().truncatedTo(ChronoUnit.DAYS),
            roles,
            new ArrayList<>(),
            new ArrayList<>(),
            true
    );

    Post mockPost = CustomTestUtils.createMockPost(mockMember);

    given(memberService.findById(anyLong())).willReturn(mockAdmin);

    given(categoryService.findByName(anyString())).willReturn(mockPost.getCategory());

    given(postService.getPost(any(Category.class), anyLong())).willReturn(mockPost);

    mockMvc.perform(
            delete("/post/{category}/{no}", mockPost.getCategory().getName(), mockPost.getPostNo())
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk()); 

    verify(postService).getPost(any(Category.class), anyLong());
  }

  // http://localhost:8080/post/download?fileUrl=asdf1234
  @Test
  @DisplayName("다운로드 테스트")
  void downloadFile_success() throws Exception {
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(CustomTestUtils.createMockMember());
    String fileUrl = "asdf1234";
    String storedFileName = "20240101_mockFile.txt";
    byte[] mockFileContent = "Mock File Content".getBytes();
    PostFile mockFile = new PostFile(
      1L, 
      fileUrl, 
      storedFileName, 
      "mockName", 
      1024*1024, 
      LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 
      null);

    given(postFileRepository.findByFileUrl(fileUrl)).willReturn(mockFile);

    given(awsS3Service.downloadFile(storedFileName)).willReturn(mockFileContent);

    mockMvc.perform(
            get("/post/download")
            .param("fileUrl", fileUrl)
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(
                HttpHeaders.CONTENT_DISPOSITION, 
                "form-data; name=\"attachment\"; filename=" + "\"" + URLEncoder.encode(storedFileName, "UTF-8") + "\""
                    .replaceAll("\\+", "%20")))
            .andExpect(content().bytes(mockFileContent));
  }
}
