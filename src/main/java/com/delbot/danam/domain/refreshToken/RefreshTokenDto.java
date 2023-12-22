package com.delbot.danam.domain.refreshToken;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RefreshTokenDto {
  //
  @NotEmpty
  String refreshToken;
}
