package com.delbot.danam.domain.refreshToken;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
  //
  Optional<RefreshToken> findByValue(String value);
}
