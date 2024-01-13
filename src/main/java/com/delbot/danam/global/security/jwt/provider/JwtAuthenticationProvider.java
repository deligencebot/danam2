package com.delbot.danam.global.security.jwt.provider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.delbot.danam.global.security.jwt.token.JwtAuthenticationToken;
import com.delbot.danam.global.security.jwt.util.JwtTokenizer;
import com.delbot.danam.global.security.jwt.util.LoginInfoDto;
import com.delbot.danam.global.util.ObjectUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider{
  //
  private final JwtTokenizer jwtTokenizer;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
    // 토큰을 검증한다. 기간이 만료되었는지, 토큰 문자열이 문제가 있는지 등 Exception이 발생한다.
    Claims claims = jwtTokenizer.parseAccessToken(authenticationToken.getToken());
    String name = claims.getSubject();
    Long memberId = claims.get("memberId", Long.class);
    String nickname = claims.get("nickname", String.class);
    List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

    LoginInfoDto loginInfo = new LoginInfoDto();
    loginInfo.setMemberId(memberId);
    loginInfo.setName(name);
    loginInfo.setNickname(nickname);

    return new JwtAuthenticationToken(authorities, loginInfo, null);
  }

  private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
    List<String> roles = ObjectUtil.convertToListString(claims.get("roles"));
    List<GrantedAuthority> authorities = new ArrayList<>();
    for (String role : roles) {
      authorities.add(() -> role);
    }
    return authorities;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return JwtAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
