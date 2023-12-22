package com.delbot.danam.domain.refreshToken;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
  //
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public RefreshToken addRefreshToken(RefreshToken refreshToken) {
    return refreshTokenRepository.save(refreshToken);
  }

  @Transactional
  public void deleteRefreshToken(String value) {
    refreshTokenRepository.findByValue(value).ifPresent(refreshTokenRepository::delete);
  }

  @Transactional(readOnly = true)
  public RefreshToken findByRefreshToken(String value) {
    return refreshTokenRepository.findByValue(value).orElseThrow(
      () -> RefreshTokenErrorCode.NOT_FOUND_REFRESH_TOKEN.defaultException()
    );
  }
}
