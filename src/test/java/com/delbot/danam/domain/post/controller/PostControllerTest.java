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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.delbot.danam.config.TestSecurityConfig;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.service.MemberService;
import com.delbot.danam.domain.post.dto.PostRequestDto;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.service.PageService;
import com.delbot.danam.domain.post.service.PostService;
import com.delbot.danam.global.security.jwt.token.JwtAuthenticationToken;
import com.delbot.danam.global.security.jwt.util.IfLoginArgumentResolver;
import com.delbot.danam.util.CustomTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = PostController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureWebMvc 
public class PostControllerTest {
  //
  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  PostService postService;

  @MockBean
  PageService pageService;

  @MockBean
  MemberService memberService;

  @MockBean
  IfLoginArgumentResolver ifLoginArgumentResolver;

  // http://localhost:8080/post/{category} (category = board)
  @Test
  @DisplayName("검색조회(전체) 테스트")
  void category_success() throws Exception {
    String category = "board";
    Page<Post> mockPage = CustomTestUtils.generateMockPage(category, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    Authentication authentication = new TestingAuthenticationToken("user0001", null, "USER");

    given(pageService.getPage(anyString(), any(Pageable.class))).willReturn(mockPage);

    mockMvc.perform(
            get("/post/{category}", category)
            .with(authentication(authentication)))            
            .andDo(print())
            .andExpect(status().isOk());

    verify(pageService).getPage(anyString(), any(Pageable.class));
  }

  // http://localhost:8080/post/{category}?target=all&keyword=aaa (category = board)
  @Test
  @DisplayName("검색조회(All조건) 테스트")
  void category_All_success() throws Exception {
    String category = "board";
    Page<Post> mockPage = CustomTestUtils.generateMockPage(category, PageRequest.of(0, 5, Sort.Direction.DESC, "postNo"));

    Authentication authentication = new TestingAuthenticationToken("user0001", null, "USER");

    given(pageService.searchByAll(anyString(), anyString(), any(Pageable.class))).willReturn(mockPage);

    mockMvc.perform(
            get("/post/{category}", category)
            .param("target", "all")
            .param("keyword", "aaa")
            .with(authentication(authentication)))            
            .andDo(print())
            .andExpect(status().isOk());

    verify(pageService).searchByAll(anyString(), anyString(), any(Pageable.class));
  }

  // http://localhost:8080/post/{category}/{no} (category = board, no = 1)
  @Test
  @DisplayName("게시글 조회 테스트")
  void viewPost_success() throws Exception {
    String category = "board";
    Long no = 1L;
    Authentication authentication = new TestingAuthenticationToken("user0001", null, "USER");

    Post mockPost = new Post(
              1L, 
              no, 
              category, 
              "Title", 
              "Hello!!", 
              0L, 
              LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 
              null, 
              new Member("user0001", "password", "홍길동", "user0001@gmail.com"));

    given(postService.getPost(anyString(), anyLong())).willReturn(mockPost);

    

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

    verify(postService).getPost(anyString(), anyLong());
  }

  // http://localhost:8080/post/{category}/{no} (category = board, no = 1)
  @Test
  @DisplayName("게시글 조회 테스트")
  void updatePost_success() throws Exception {
    String category = "board";
    Long no = 1L;
    Authentication authentication = new TestingAuthenticationToken("user0001", null, "USER");

    Member mockMember = CustomTestUtils.createMockMember();
    Post mockPost = CustomTestUtils.createMockPost(mockMember);

    given(postService.getPost(anyString(), anyLong())).willReturn(mockPost);

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
    Post mockPost = new Post(1L, 1L, "board", "Title", "Hello World!", 0L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), null, mockMember);

    String category = "board";
    Long no = 1L;
    PostRequestDto request = new PostRequestDto();
    request.setTitle("updated title");
    request.setContents("updated Hello world!!");

    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);

    given(memberService.findById(anyLong())).willReturn(mockMember);

    given(postService.getPost(anyString(), anyLong())).willReturn(mockPost);

    mockPost.update(request.getTitle(), request.getContents());

    given(postService.addPost(any())).willReturn(mockPost);

    mockMvc.perform(
            put("/post/{category}/{no}", category, no)
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(jwtAuthenticationToken)))
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
    
    assertEquals(request.getTitle(), mockPost.getTitle());
    assertEquals(request.getContents(), mockPost.getContents());
    
    verify(postService).addPost(any());
  }

  // http://localhost:8080/post/{category}/posting (category = board)
  @Test
  @DisplayName("게시글 작성 테스트")
  void posting_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);
    Post mockPost = new Post(1L, 1L, "board", "Title", "Hello World!", 0L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), null, mockMember);

    String category = "board";
    PostRequestDto postRequestDto = new PostRequestDto();
    postRequestDto.setTitle("Title");
    postRequestDto.setContents("Hello World!!");

    given(memberService.findById(anyLong())).willReturn(mockMember);

    given(postService.addPost(any(Post.class))).willReturn(mockPost);

    mockMvc.perform(
            post("/post/{category}/posting", category)
            .content(CustomTestUtils.toJson(postRequestDto))
            .contentType(MediaType.APPLICATION_JSON)
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
            .andExpect(jsonPath("$.createdTime").exists());

    verify(postService).addPost(any(Post.class));
  }

  // http://localhost:8080/post/{category}/{no} (category = board, no = 1)
  @Test
  @DisplayName("게시글 작성 테스트")
  void deletePost_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);
    Post mockPost = new Post(1L, 1L, "board", "Title", "Hello World!", 0L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), null, mockMember);

    given(memberService.findById(anyLong())).willReturn(mockMember);

    given(postService.getPost(anyString(), anyLong())).willReturn(mockPost);

    mockMvc.perform(
            delete("/post/{category}/{no}", mockPost.getCategory(), mockPost.getPostNo())
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk()); 

    verify(postService).getPost(anyString(), anyLong());
  }
}
