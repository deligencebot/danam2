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

import java.util.Optional;
import java.util.Collections;
import java.lang.String;

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
import com.delbot.danam.domain.member.dto.MemberLoginRequestDto;
import com.delbot.danam.domain.member.dto.MemberSignupRequestDto;
import com.delbot.danam.domain.member.dto.MemberUpdateRequestDto;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.service.MemberService;
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
  IfLoginArgumentResolver ifLoginArgumentResolver;

  // http://localhost:8080/members/signup
  @Test
  @DisplayName("회원가입 테스트")
  void signup_success() throws Exception {
    Member mockMember = CustomTestUtils.createMockMember();
    Authentication authentication = new TestingAuthenticationToken(mockMember.getName(), null, "USER");

    MemberSignupRequestDto request = MemberSignupRequestDto.builder()
            .name(mockMember.getName())
            .password(mockMember.getPassword())
            .passwordCheck(mockMember.getPassword())
            .nickname(mockMember.getNickname())
            .email(mockMember.getEmail())
            .build();

    given(memberService.addMember(any(Member.class))).willReturn(mockMember);

    mockMvc.perform(
            post("/members/signup")
                .content(CustomTestUtils.toJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(authentication(authentication)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.memberId").exists())
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

    MemberLoginRequestDto request = MemberLoginRequestDto.builder()
            .name(mockMember.getName())
            .password(mockMember.getPassword())
            .build();

    given(memberService.findByName(anyString())).willReturn(Optional.of(mockMember));

    given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

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
            .andExpect(jsonPath("$.memberId").exists())
            .andExpect(jsonPath("$.name").exists())
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
            .andExpect(jsonPath("$.nickname").exists())
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
                .content(CustomTestUtils.toJson(loginUserDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(authentication(jwtAuthenticationToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.memberId").exists())
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
    Claims claims = mock(Claims.class);
    RefreshTokenDto refreshTokenDto = new RefreshTokenDto("SampleRefreshToken");
    RefreshToken refreshToken = new RefreshToken(refreshTokenDto.getRefreshToken(), mockMember.getMemberId());

    MemberUpdateRequestDto memberUpdateRequestDto = new MemberUpdateRequestDto();
    memberUpdateRequestDto.setPassword("asdf1234!");
    memberUpdateRequestDto.setNickname("홍길동0001b");
    memberUpdateRequestDto.setEmail("user0001@google.com");
    memberUpdateRequestDto.setRefreshToken("SampleToken");

    Authentication authentication = new TestingAuthenticationToken(mockMember.getName(), null, "USER");

    given(refreshTokenService.findByRefreshToken(anyString())).willReturn(refreshToken);

    given(jwtTokenizer.parseRefreshToken(anyString())).willReturn(claims);

    given(claims.get("memberId")).willReturn(mockMember.getMemberId());

    given(memberService.findById(anyLong())).willReturn(mockMember);

    mockMember.updateDetails(memberUpdateRequestDto.getNickname(), memberUpdateRequestDto.getEmail());
  
    given(memberService.addMember(any(Member.class))).willReturn(mockMember);

    given(jwtTokenizer.createAccessToken(anyLong(), anyString(), anyString(), anyString(), anyList()))
            .willReturn("SampleAccessToken");
    
    given(jwtTokenizer.createRefreshToken(anyLong(), anyString(), anyString(), anyString(), anyList()))
            .willReturn("SampleRefreshToken");

    doNothing().when(refreshTokenService).deleteRefreshToken(anyString());

    given(refreshTokenService.addRefreshToken(any(RefreshToken.class))).willReturn(mock(RefreshToken.class));
    
    mockMvc.perform(
            post("/members/details")
            .content(CustomTestUtils.toJson(memberUpdateRequestDto))
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

    verify(memberService).addMember(any(Member.class));
    verify(refreshTokenService).deleteRefreshToken(anyString());
    verify(refreshTokenService).addRefreshToken(any(RefreshToken.class));
  }
}
