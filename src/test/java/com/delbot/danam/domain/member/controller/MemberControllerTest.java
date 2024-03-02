package com.delbot.danam.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.delbot.danam.config.TestSecurityConfig;
import com.delbot.danam.domain.comment.entity.Comment;
import com.delbot.danam.domain.comment.service.CommentService;
import com.delbot.danam.domain.member.dto.MemberRequestDto;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.service.MemberService;
import com.delbot.danam.domain.post.entity.Post;
import com.delbot.danam.domain.post.service.PostService;
import com.delbot.danam.domain.refreshToken.RefreshToken;
import com.delbot.danam.domain.refreshToken.RefreshTokenDto;
import com.delbot.danam.domain.refreshToken.RefreshTokenService;
import com.delbot.danam.global.security.jwt.token.JwtAuthenticationToken;
import com.delbot.danam.global.security.jwt.util.IfLoginArgumentResolver;
import com.delbot.danam.global.security.jwt.util.JwtTokenizer;
import com.delbot.danam.global.security.jwt.util.LoginUserDto;
import com.delbot.danam.global.util.ObjectUtil;
import com.delbot.danam.util.CustomTestUtils;

import io.jsonwebtoken.Claims;

@WebMvcTest(controllers = MemberController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureWebMvc 
public class MemberControllerTest {
  //
  @Autowired
  MockMvc mockMvc;

  @MockBean
  MemberService memberService;

  @MockBean
  RefreshTokenService refreshTokenService;

  @MockBean
  JwtTokenizer jwtTokenizer;

  @MockBean
  PasswordEncoder passwordEncoder;

  @MockBean
  PostService postService;
  
  @MockBean
  CommentService commentService;

  @MockBean
  IfLoginArgumentResolver ifLoginArgumentResolver;

  // http://localhost:8080/members/signup
  @Test
  @DisplayName("회원가입 테스트")
  void signup_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    Authentication authentication = new TestingAuthenticationToken(mockMember.getName(), null, "USER");

    MemberRequestDto.Signup request = new MemberRequestDto.Signup();
    request.setName(mockMember.getName());
    request.setPassword(mockMember.getPassword());
    request.setPasswordCheck(mockMember.getPassword());
    request.setNickname(mockMember.getNickname());
    request.setEmail(mockMember.getEmail());

    given(memberService.addMember(any(Member.class))).willReturn(mockMember);

    mockMvc.perform(
            post("/members/signup")
                .content(CustomTestUtils.toJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(authentication(authentication)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath( "$.nickname").exists())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.createdDate").exists())
            .andDo(print());

    verify(memberService).addMember(any(Member.class));
  }

  // http://localhost:8080/members/login
  @Test
  @DisplayName("로그인 테스트")
  void login_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    Authentication authentication = new TestingAuthenticationToken(mockMember.getName(), null, "USER");
    String sampleAccessToken = "AccessToken";
    String sampleRefreshToken = "RefreshToken";

    MemberRequestDto.Login request = new MemberRequestDto.Login();
    request.setName(mockMember.getName());
    request.setPassword(mockMember.getPassword());

    given(memberService.login(anyString(), anyString())).willReturn((mockMember));

    given(jwtTokenizer.createAccessToken(anyLong(), anyString(), anyString(), anyString(), anyList()))
            .willReturn(sampleAccessToken);
    
    given(jwtTokenizer.createRefreshToken(anyLong(), anyString(), anyString(), anyString(), anyList()))
            .willReturn(sampleRefreshToken);

    mockMvc.perform(
            post("/members/login")
                .content(CustomTestUtils.toJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(authentication(authentication)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.memberId").exists())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.nickname").exists())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.createdDate").exists())
            .andDo(print());

    verify(jwtTokenizer).createAccessToken(anyLong(), anyString(), anyString(), anyString(), anyList());
    verify(jwtTokenizer).createRefreshToken(anyLong(), anyString(), anyString(), anyString(), anyList());
  }

  // http://localhost:8080/members/logout
  @Test
  @DisplayName("로그아웃 테스트")
  void logout_success() throws Exception {
    RefreshTokenDto refreshTokenDto = new RefreshTokenDto("sampleToken");
    Authentication authentication = new TestingAuthenticationToken("user0001", null, "USER");

    // 사용자의 인증 정보를 설정
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

    doNothing().when(refreshTokenService).deleteRefreshToken(anyString());

    mockMvc.perform(
            delete("/members/logout")
                .content(CustomTestUtils.toJson(refreshTokenDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(authentication(authentication)))
            .andExpect(status().isOk())
            .andDo(print());

    verify(refreshTokenService).deleteRefreshToken(anyString());
  }

  // http://localhost:8080/members/refreshToken
  @Test
  @DisplayName("토큰갱신 테스트")
  void refreshToken_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    RefreshTokenDto refreshTokenDto = new RefreshTokenDto("SampleRefreshToken");
    RefreshToken refreshToken = new RefreshToken(refreshTokenDto.getRefreshToken(), mockMember.getMemberId());
    Claims claims = mock(Claims.class);
    Authentication authentication = new TestingAuthenticationToken(mockMember.getName(), null, "USER");

    given(refreshTokenService.findByRefreshToken(anyString())).willReturn(refreshToken);

    given(claims.get("memberId")).willReturn(mockMember.getMemberId());

    given(claims.getSubject()).willReturn(mockMember.getName());

    given(claims.get("nickname")).willReturn(mockMember.getNickname());

    given(claims.get("email")).willReturn(mockMember.getEmail());

    given(ObjectUtil.convertToListString(claims.get("roles"))).willReturn(Collections.singletonList("ROLE_USER"));

    given(jwtTokenizer.parseRefreshToken(anyString())).willReturn(claims);

    given(memberService.findById(anyLong())).willReturn(mockMember);

    given(jwtTokenizer.createAccessToken(anyLong(), anyString(), anyString(), anyString(), anyList())).willReturn("SampleAccessToken");

    mockMvc.perform(
            post("/members/refreshToken")
                .content(CustomTestUtils.toJson(refreshTokenDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(authentication(authentication)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.memberId").exists())
            .andDo(print());

    verify(jwtTokenizer).createAccessToken(anyLong(), anyString(), anyString(), anyString(), anyList());
  }

  // http://localhost:8080/members/details
  @Test
  @DisplayName("상세정보 테스트")
  void detail_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    LoginUserDto loginUserDto = CustomTestUtils.getLoginUserDto(mockMember);
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);

    given(memberService.findById(anyLong())).willReturn(mockMember);

    mockMvc.perform(
            get("/members/details")
                .requestAttr("loginUserDto", loginUserDto)
                .with(authentication(jwtAuthenticationToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.nickname").exists())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.createdDate").exists())
            .andDo(print());

     verify(memberService).findById(anyLong());
  }

  // http://localhost:8080/members/details
  @Test
  @DisplayName("정보수정 테스트")
  void updateDetails_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    Member updatedMockMember = CustomTestUtils.createMockMember();

    MemberRequestDto.Update request = new MemberRequestDto.Update();
    request.setNickname("홍길동0001b");
    request.setEmail("user0001@google.com");
    request.setRefreshToken("SampleToken");

    updatedMockMember.updateDetails(request.getNickname(), request.getEmail());

    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);

    given(memberService.findById(anyLong())).willReturn(mockMember);
  
    given(memberService.addMember(any(Member.class))).willReturn(updatedMockMember);

    given(jwtTokenizer.createAccessToken(anyLong(), anyString(), anyString(), anyString(), anyList()))
            .willReturn("SampleAccessToken");
    
    given(jwtTokenizer.createRefreshToken(anyLong(), anyString(), anyString(), anyString(), anyList()))
            .willReturn("SampleRefreshToken");

    doNothing().when(refreshTokenService).deleteRefreshToken(anyString());

    given(refreshTokenService.addRefreshToken(any(RefreshToken.class))).willReturn(mock(RefreshToken.class));
    
    mockMvc.perform(
            post("/members/details")
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(jwtAuthenticationToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.memberId").exists())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.nickname").exists())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.createdDate").exists())
            .andDo(print());

    verify(memberService).addMember(any(Member.class));
    verify(refreshTokenService).deleteRefreshToken(anyString());
    verify(refreshTokenService).addRefreshToken(any(RefreshToken.class));
  }

  // http://localhost:8080/members/valid
  @Test
  @DisplayName("회원검증 테스트")
  void checkValid_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);
    
    MemberRequestDto.CheckPassword request = new MemberRequestDto.CheckPassword();
    request.setPassword("mockPassword12!");
    
    given(memberService.findById(anyLong())).willReturn(mockMember);

    given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

    mockMvc.perform(
            post("/members/valid")
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk());
  }

  // http://localhost:8080/members/password
  @Test
  @DisplayName("비밀번호 변경 테스트")
  void alterPassword_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    Member updatedMockMember = CustomTestUtils.createMockMember();
    updatedMockMember.updatePassword("newPassword1234");

    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);

    MemberRequestDto.AlterPassword request = new MemberRequestDto.AlterPassword();
    request.setExistingPassword("exPassword1234!");
    request.setNewPassword("newPassword1234!");
    request.setNewPasswordCheck("newPassword1234!");
    request.setRefreshToken("FakeToken");

    given(memberService.findById(anyLong())).willReturn(mockMember);

    given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

    given(memberService.addMember(mockMember)).willReturn(updatedMockMember);

    given(jwtTokenizer.createAccessToken(anyLong(), anyString(), anyString(), anyString(), anyList()))
            .willReturn("SampleAccessToken");
    
    given(jwtTokenizer.createRefreshToken(anyLong(), anyString(), anyString(), anyString(), anyList()))
            .willReturn("SampleRefreshToken");

    doNothing().when(refreshTokenService).deleteRefreshToken(anyString());

    given(refreshTokenService.addRefreshToken(any(RefreshToken.class))).willReturn(mock(RefreshToken.class));

    mockMvc.perform(
            post("/members/password")
            .content(CustomTestUtils.toJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.memberId").exists())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.nickname").exists())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.createdDate").exists());

    verify(memberService).addMember(any(Member.class));
    verify(refreshTokenService).deleteRefreshToken(anyString());
    verify(refreshTokenService).addRefreshToken(any(RefreshToken.class));
  }

  // http://localhost:8080/members/info
  @Test
  @DisplayName("회원정보조회 테스트")
  void lookupMemberInfo_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    JwtAuthenticationToken jwtAuthenticationToken = CustomTestUtils.getLoginUserJwtAuthenticationToken(mockMember);
    List<Post> postList = new ArrayList<>();
    for (Long l = 0L; l < 20; l++) {
      postList.add(new Post(
              l, 
              l, 
              CustomTestUtils.categoryMockCategory("board"), 
              "Title" + String.valueOf(l), 
              "Hello!!", 
              0L,
              false,
              false,
              true,
              LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 
              LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plus(Duration.ofMillis(60 * 60 * 1000L)), 
              mockMember,
              null,
              null,
              null
      ));
    }

    List<Comment> commentList = new ArrayList<>();
    for (Long l = 0L; l < 20; l++) {
      commentList.add(new Comment(
        l,
        "Content" + String.valueOf(l),
        mockMember,
        null,
        null,
        1,
        false,
        false,
        null,
        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 
        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plus(Duration.ofMillis(60 * 60 * 1000L))
      ));
    }

    given(memberService.findByNickname(anyString())).willReturn(Optional.of(mockMember));

    given(postService.getMemberInfoPosts(mockMember)).willReturn(postList);

    given(commentService.getMemberInfoComments(mockMember)).willReturn(commentList);

    mockMvc.perform(
            get("/members/info")
            .param("nickname", "mockMember")
            .with(authentication(jwtAuthenticationToken)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.nickname").exists())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.createdDate").exists())
            .andExpect(jsonPath("$.memberPostList").exists())
            .andExpect(jsonPath("$.memberCommentList").exists());
  }
}
