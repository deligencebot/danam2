package com.delbot.danam.domain.member.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.delbot.danam.domain.comment.dto.CommentResponseDto;
import com.delbot.danam.domain.comment.service.CommentService;
import com.delbot.danam.domain.member.dto.MemberRequestDto;
import com.delbot.danam.domain.member.dto.MemberResponseDto;
import com.delbot.danam.domain.member.entity.Member;
import com.delbot.danam.domain.member.exception.MemberErrorCode;
import com.delbot.danam.domain.member.service.MemberService;
import com.delbot.danam.domain.post.dto.PostResponseDto;
import com.delbot.danam.domain.post.service.PostService;
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
  private final PostService postService;
  private final CommentService commentService;

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody @Valid MemberRequestDto.Signup request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw MemberErrorCode.INVALID_INPUT_VALUE.defaultException();
    }

    if (!request.getPassword().equals(request.getPasswordCheck())) {
      bindingResult.rejectValue("passwordCheck", "NOT_EQUAL_PASSWORD_CHECK", "비밀번호가 일치하지 않습니다.");
      throw MemberErrorCode.NOT_EQUAL_PASSWORD_CHECK.defaultException();
    }

    if (memberService.findByName(request.getName()).isPresent()) {
      bindingResult.rejectValue("name", "DUPLICATED_NAME", "이미 사용중인 아이디입니다.");
      throw MemberErrorCode.DUPLICATED_NAME.defaultException();
    }

    if (memberService.findByNickname(request.getNickname()).isPresent()) {
      bindingResult.rejectValue("nickname", "DUPLICATED_NICKNAME", "이미 사용중인 별명입니다.");
      throw MemberErrorCode.DUPLICATED_NICKNAME.defaultException();
    }

    if (memberService.findByEmail(request.getEmail()).isPresent()) {
      bindingResult.rejectValue("email", "DUPLICATED_EMAIL", "이미 사용중인 이메일입니다.");
      throw MemberErrorCode.DUPLICATED_EMAIL.defaultException();
    }

    Member member = Member.builder()
            .name(request.getName())
            .password(passwordEncoder.encode(request.getPassword()))
            .nickname(request.getNickname())
            .email(request.getEmail())
            .build();
    
    Member savedMember = memberService.addMember(member);

    MemberResponseDto.Summary signupResponse = MemberResponseDto.Summary.builder()
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
  public ResponseEntity<?> login(@RequestBody @Valid MemberRequestDto.Login request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw MemberErrorCode.INVALID_INPUT_VALUE.defaultException();
    }

    Member member = memberService.login(request.getName(), request.getPassword());

    List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());

    String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), member.getName(), member.getNickname(), member.getEmail(), roles);
    String refreshToken = jwtTokenizer.createRefreshToken(member.getMemberId(), member.getName(), member.getNickname(), member.getEmail(), roles);

    RefreshToken refreshTokenEntity = RefreshToken.builder()
            .value(refreshToken)
            .memberId(member.getMemberId())
            .build();
    refreshTokenService.addRefreshToken(refreshTokenEntity);

    MemberResponseDto.Details loginResponse = MemberResponseDto.Details.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .memberId(member.getMemberId())
            .name(member.getName())
            .nickname(member.getNickname())
            .email(member.getEmail())
            .createdDate(member.getCreatedDate())
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

    MemberResponseDto.Token loginResponse = MemberResponseDto.Token.builder()
            .memberId(member.getMemberId())
            .accessToken(accessToken)
            .refreshToken(refreshTokenDto.getRefreshToken())
            .build();

    return new ResponseEntity<>(loginResponse, HttpStatus.OK);
  }

  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @GetMapping("/details")
  public ResponseEntity<?> getDetails(@IfLogin LoginUserDto loginUserDto) {
    Member member = memberService.findById(loginUserDto.getMemberId());

    MemberResponseDto.Summary detailResponse = MemberResponseDto.Summary.builder()
            .name(member.getName())
            .nickname(member.getNickname())
            .email(member.getEmail())
            .createdDate(member.getCreatedDate())
            .build();

    return new ResponseEntity<>(detailResponse, HttpStatus.OK);
  }

  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @Transactional
  @PostMapping("/details")
  public ResponseEntity<?> updateDetails(@RequestBody @Valid MemberRequestDto.Update request, BindingResult bindingResult, @IfLogin LoginUserDto loginUserDto) {
    if (bindingResult.hasErrors()) {
      throw MemberErrorCode.INVALID_INPUT_VALUE.defaultException();
    }

    // Member를 불러온다.
    Member member = memberService.findById(loginUserDto.getMemberId());

    // Member를 수정하고 저장
    member.updateDetails(request.getNickname(), request.getEmail());
    Member updatedMember = memberService.addMember(member);

    // RefreshToken을 수정하고 저장
    List<String> roles = updatedMember.getRoles().stream().map(Role::getName).collect(Collectors.toList());
    String newAccessToken = jwtTokenizer.createAccessToken(updatedMember.getMemberId(), updatedMember.getName(), updatedMember.getNickname(), updatedMember.getEmail(), roles);
    String newRefreshToken = jwtTokenizer.createRefreshToken(updatedMember.getMemberId(), updatedMember.getName(), updatedMember.getNickname(), updatedMember.getEmail(), roles);
    RefreshToken newRefreshTokenEntity = RefreshToken.builder()
            .value(newRefreshToken)
            .memberId(updatedMember.getMemberId())
            .build();
    refreshTokenService.deleteRefreshToken(request.getRefreshToken());
    refreshTokenService.addRefreshToken(newRefreshTokenEntity);

    // 수정된 Member, AccessToken, RefreshToken을 보내준다.
    MemberResponseDto.Details updateResponse = MemberResponseDto.Details.builder()
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

  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @PostMapping("/valid")
  public ResponseEntity<?> checkValid(@Valid @RequestBody MemberRequestDto.CheckPassword request, BindingResult bindingResult, @IfLogin LoginUserDto loginUserDto) {
    if (bindingResult.hasErrors()) {
      throw MemberErrorCode.INVALID_INPUT_VALUE.defaultException();
    }

    Member member = memberService.findById(loginUserDto.getMemberId());

    if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
      throw MemberErrorCode.WRONG_PASSWORD.defaultException();
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @Transactional
  @PostMapping("/password")
  public ResponseEntity<?> alterPassword(@Valid @RequestBody MemberRequestDto.AlterPassword request, BindingResult bindingResult, @IfLogin LoginUserDto loginUserDto) {
    if (bindingResult.hasErrors()) {
      throw MemberErrorCode.INVALID_INPUT_VALUE.defaultException();
    }

    if (!request.getNewPassword().equals(request.getNewPasswordCheck())) {
      throw MemberErrorCode.NOT_EQUAL_PASSWORD_CHECK.defaultException();
    }

    Member member = memberService.findById(loginUserDto.getMemberId());

    if (!passwordEncoder.matches(request.getExistingPassword(), member.getPassword())) {
      throw MemberErrorCode.WRONG_PASSWORD.defaultException();
    }

    member.updatePassword(request.getNewPassword());
    Member updatedMember = memberService.addMember(member);

    List<String> roles = updatedMember.getRoles().stream().map(Role::getName).collect(Collectors.toList());
    String newAccessToken = jwtTokenizer.createAccessToken(updatedMember.getMemberId(), updatedMember.getName(), updatedMember.getNickname(), updatedMember.getEmail(), roles);
    String newRefreshToken = jwtTokenizer.createRefreshToken(updatedMember.getMemberId(), updatedMember.getName(), updatedMember.getNickname(), updatedMember.getEmail(), roles);
    RefreshToken newRefreshTokenEntity = RefreshToken.builder()
            .value(newRefreshToken)
            .memberId(updatedMember.getMemberId())
            .build();
    refreshTokenService.deleteRefreshToken(request.getRefreshToken());
    refreshTokenService.addRefreshToken(newRefreshTokenEntity);

    MemberResponseDto.Details updateResponse = MemberResponseDto.Details.builder()
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

  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @Transactional
  @GetMapping("/info")
  public ResponseEntity<?> lookupMemberInfo(@RequestParam String nickname) {

    Member member = memberService.findByNickname(nickname).orElseThrow(
            () -> MemberErrorCode.NOT_FOUND_MEMBER.defaultException());

    List<PostResponseDto.Pages> postDtoList = postService.getMemberInfoPosts(member).stream().map(post -> {
      return PostResponseDto.Pages.builder()
              .postId(post.getPostId())
              .postNo(post.getPostNo())
              .category(post.getCategory().getName())
              .title(post.getTitle())
              .writer(member.getNickname())
              .hits(post.getHits())
              .createdTime(post.getCreatedTime())
              .updatedTime(post.getUpdatedTime())
              .build();
    }).collect(Collectors.toList());

    List<CommentResponseDto> commentDtoList = commentService.getMemberInfoComments(member).stream().map(comment -> {
      return CommentResponseDto.builder()
              .commentId(comment.getCommentId())
              .writer(member.getNickname())
              .contents(comment.getContents())
              .depth(comment.getDepth())
              .createdTime(comment.getCreatedTime())
              .updatedTime(comment.getUpdatedTime())
              .isUpdated(comment.isUpdated())
              .isDeleted(comment.isDeleted())
              .build();
    }).collect(Collectors.toList());

    MemberResponseDto.Info infoResponse = MemberResponseDto.Info.builder()
            .name(member.getName())
            .nickname(member.getNickname())
            .email(member.getEmail())
            .createdDate(member.getCreatedDate())
            .memberPostList(postDtoList)
            .memberCommentList(commentDtoList)
            .build();

    return new ResponseEntity<>(infoResponse, HttpStatus.OK);
  } 
}
