package com.delbot.danam.domain.refreshToken;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import com.delbot.danam.global.security.jwt.util.JwtProperties;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@RedisHash(value = "refreshToken", timeToLive = JwtProperties.REFRESH_TOKEN_EXPIRE_TIME)
@Getter
public class RefreshToken {
  //
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Indexed
  private String value;
  @Indexed
  private Long memberId;

  @Builder
  public RefreshToken(final String value, final Long memberId) {
    this.value = value;
    this.memberId = memberId;
  }
}
