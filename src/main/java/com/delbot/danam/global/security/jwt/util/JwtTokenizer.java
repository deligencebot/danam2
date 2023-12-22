package com.delbot.danam.global.security.jwt.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenizer {
  //
  private final byte[] accessSecret;
  private final byte[] refreshSecret;

  public JwtTokenizer(@Value("${jwt.accessSecret}") String accessSecret, @Value("${jwt.refreshSecret}") String refreshSecret) {
    this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
    this.refreshSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
  }

  /**
   * AccessToken 생성
   */
  public String createAccessToken(Long id, String name, String nickname, String email, List<String> roles) {
    return createToken(id, name, nickname, email, roles, JwtProperties.ACCESS_EXPIRATION_TIME, accessSecret);
  }

  /**
   * RefreshToken 생성
   */
  public String createRefreshToken(Long id, String name, String nickname, String email, List<String> roles) {
    return createToken(id, name, nickname, email, roles, JwtProperties.REFRESH_TOKEN_EXPIRE_TIME, refreshSecret);
  }

  private String createToken(Long id, String name, String nickname, String email, List<String> roles, long expire, byte[] secretKey) {
    Claims claims = Jwts.claims().setSubject(name);

    claims.put("roles", roles);
    claims.put("memberId", id);
    claims.put("nickname", nickname);
    claims.put("email", email);

    return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + expire))
            .signWith(getSigningKey(secretKey))
            .compact();
  }

  /**
   * Get Token
   */
  public Long getMemberIdFromToken(String token) {
    String[] tokenArray = token.split(" ");
    token = tokenArray[1];
    Claims claims = parseToken(token, accessSecret);
    return Long.valueOf((Integer)claims.get("memberId"));
  }

  public Claims parseAccessToken(String accessToken) {
    return parseToken(accessToken, accessSecret);
  }

  public Claims parseRefreshToken(String refreshToken) {
    return parseToken(refreshToken, refreshSecret);
  }

  public Claims parseToken(String token, byte[] secretKey) {
    return Jwts.parserBuilder()
            .setSigningKey(getSigningKey(secretKey))
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  /**
   * @param secretKey - byte형식
   * @return Key 형식 시크릿 키
   */
  private Key getSigningKey(byte[] secretKey) {
    return Keys.hmacShaKeyFor(secretKey);
  }

}
