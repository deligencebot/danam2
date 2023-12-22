package com.delbot.danam.domain.refreshToken;

import com.delbot.danam.global.common.exception.CustomException;
import com.delbot.danam.global.common.exception.ErrorCode;

public class RefreshTokenException extends CustomException {
  //
    public RefreshTokenException() {
        super();
    }

    public RefreshTokenException(String message) {
        super(message);
    }

    public RefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RefreshTokenException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
