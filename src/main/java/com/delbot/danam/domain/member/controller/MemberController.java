package com.delbot.danam.domain.member.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delbot.danam.domain.member.dto.MemberLoginRequestDto;
import com.delbot.danam.domain.member.dto.MemberResponseDto;
import com.delbot.danam.domain.member.dto.MemberSignupRequestDto;
import com.delbot.danam.domain.member.dto.MemberUpdateRequestDto;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.exception.MemberErrorCode;
import com.delbot.danam.domain.member.service.MemberService;
import com.delbot.danam.domain.refreshToken.RefreshToken;
import com.delbot.danam.domain.refreshToken.RefreshTokenDto;
import com.delbot.danam.domain.refreshToken.RefreshTokenService;
import com.delbot.danam.domain.role.Role;
import com.delbot.danam.global.security.jwt.util.IfLogin;
import com.delbot.danam.global.security.jwt.util.JwtTokenizer;
import com.delbot.danam.global.security.jwt.util.LoginUserDto;
import com.delbot.danam.global.util.ObjectUtil;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
  //
  private final MemberService memberService;
  private final RefreshTokenService refreshTokenService;
  private final JwtTokenizer jwtTokenizer;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody @Valid MemberSignupRequestDto request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw MemberErrorCode.INVALID_VALUE.defaultException();
    }

    if (!request.getPassword().equals(request.getPasswordCheck())) {
      throw MemberErrorCode.WRONG_PASSWORD_CHECK.defaultException();
    }

    if (memberService.findByName(request.getName()).isPresent()) {
      throw MemberErrorCode.DUPLICATED_MEMBER.defaultException();
    }

    if (memberService.findByNickname(request.getNickname()).isPresent()) {
      throw MemberErrorCode.DUPLICATED_NICKNAME.defaultException();
    }

    if (memberService.findByEmail(request.getEmail()).isPresent()) {
      throw MemberErrorCode.DUPLICATED_EMAIL.defaultException();
    }

    Member member = Member.builder()
            .name(request.getName())
            .password(passwordEncoder.encode(request.getPassword()))
            .nickname(request.getNickname())
            .email(request.getEmail())
            .build();
    
    Member savedMember = memberService.addMember(member);

    MemberResponseDto.Info signupResponse = MemberResponseDto.Info.builder()
            .memberId(savedMember.getMemberId())
            .name(savedMember.getName())
            .nickname(savedMember.getNickname())
            .email(savedMember.getEmail())
            .createdDate(savedMember.getCreatedDate())
            .build();

    log.info("=========================================");
    log.info("Signup complete!!");
    log.info("{}", savedMember.toString());
    log.info("=========================================");

    return new ResponseEntity<>(signupResponse, HttpStatus.CREATED);
  }

  @Transactional
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid MemberLoginRequestDto request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw MemberErrorCode.INVALID_VALUE.defaultException();
    }

    Optional<Member> optionalMember = memberService.findByName(request.getName());
    if (!optionalMember.isPresent()) {
      throw MemberErrorCode.NOT_FOUND_MEMBER.defaultException();
    }
    Member member = optionalMember.get();
    
    if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
      throw MemberErrorCode.WRONG_PASSWORD.defaultException();
    }

    List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());

    String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), member.getName(), member.getNickname(), member.getEmail(), roles);
    String refreshToken = jwtTokenizer.createRefreshToken(member.getMemberId(), member.getName(), member.getNickname(), member.getEmail(), roles);

    RefreshToken refreshTokenEntity = RefreshToken.builder()
            .value(refreshToken)
            .memberId(member.getMemberId())
            .build();
    refreshTokenService.addRefreshToken(refreshTokenEntity);

    MemberResponseDto.Response loginResponse = MemberResponseDto.Response.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .memberId(member.getMemberId())
            .name(member.getName())
            .build();
    
    log.info("=========================================");
    log.info("Login complete!!");
    log.info("Member : {}", member.getName());
    log.info("accessToken : {}", accessToken);
    log.info("refreshToken : {}", refreshToken);
    log.info("=========================================");

    return new ResponseEntity<>(loginResponse, HttpStatus.OK);
  }

  @DeleteMapping("/logout")
  public ResponseEntity<?> logout(@RequestBody RefreshTokenDto refreshTokenDto) {
    refreshTokenService.deleteRefreshToken(refreshTokenDto.getRefreshToken());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // Refresh AccessToken
  @PostMapping("/refreshToken")
  public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
    RefreshToken refreshToken = refreshTokenService.findByRefreshToken(refreshTokenDto.getRefreshToken());

    Claims claims = jwtTokenizer.parseRefreshToken(refreshToken.getValue());
    Long memberId = Long.valueOf(String.valueOf(claims.get("memberId")));

    Member member = memberService.findById(memberId);

    List<String> roles = ObjectUtil.convertToListString(claims.get("roles"));
    String name = claims.getSubject();
    String nickname = claims.get("nickname").toString();
    String email = claims.get("email").toString();

    String accessToken = jwtTokenizer.createAccessToken(memberId, name, nickname, email, roles);

    MemberResponseDto.Response loginResponse = MemberResponseDto.Response.builder()
            .accessToken(accessToken)
            .refreshToken(refreshTokenDto.getRefreshToken())
            .memberId(member.getMemberId())
            .nickname(nickname)
            .build();
    return new ResponseEntity<>(loginResponse, HttpStatus.OK);
  }

  @GetMapping("/details")
  public ResponseEntity<?> getDetails(@IfLogin LoginUserDto loginUserDto) {

    log.info(loginUserDto.toString());

    Member member = memberService.findById(loginUserDto.getMemberId());

    MemberResponseDto.Info detailResponse = MemberResponseDto.Info.builder()
            .name(member.getName())
            .memberId(member.getMemberId())
            .nickname(member.getNickname())
            .email(member.getEmail())
            .createdDate(member.getCreatedDate())
            .build();
    return new ResponseEntity<>(detailResponse, HttpStatus.OK);
  }

  @Transactional
  @PostMapping("/details")
  public ResponseEntity<?> updateDetails(@RequestBody @Valid MemberUpdateRequestDto request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw MemberErrorCode.INVALID_VALUE.defaultException();
    }

    // Member를 불러온다.
    RefreshToken refreshToken = refreshTokenService.findByRefreshToken(request.getRefreshToken());

    Claims claims = jwtTokenizer.parseRefreshToken(refreshToken.getValue());
    Long memberId = Long.valueOf(String.valueOf(claims.get("memberId")));

    Member member = memberService.findById(memberId);

    // Member를 수정하고 저장
    member.updateDetails(request.getNickname(), request.getEmail());
    Member updatedMember = memberService.addMember(member);

    // RefreshToken을 수정하고 저장
    List<String> roles = updatedMember.getRoles().stream().map(Role::getName).collect(Collectors.toList());
    String newAccessToken = jwtTokenizer.createAccessToken(updatedMember.getMemberId(), updatedMember.getName(), updatedMember.getNickname(), updatedMember.getEmail(), roles);
    String newRefreshToken = jwtTokenizer.createRefreshToken(updatedMember.getMemberId(), updatedMember.getName(), updatedMember.getNickname(), updatedMember.getEmail(), roles);
    RefreshToken newRefreshTokenEntity = RefreshToken.builder()
            .value(newRefreshToken)
            .memberId(memberId)
            .build();
    refreshTokenService.deleteRefreshToken(request.getRefreshToken());
    refreshTokenService.addRefreshToken(newRefreshTokenEntity);

    // 수정된 Member, AccessToken, RefreshToken을 보내준다.
    MemberResponseDto.Response updateResponse = MemberResponseDto.Response.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .memberId(updatedMember.getMemberId())
            .name(updatedMember.getName())
            .nickname(updatedMember.getNickname())
            .email(updatedMember.getEmail())
            .createdDate(updatedMember.getCreatedDate())
            .build();

    return new ResponseEntity<>(updateResponse, HttpStatus.OK);
  }
}
