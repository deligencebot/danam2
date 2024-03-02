package com.delbot.danam.global.common.exception;

public class GlobalException extends CustomException {
  //
  public GlobalException() {
    super();
  }

  public GlobalException(String message) {
    super(message);
  }

  public GlobalException(String message, Throwable cause) {
    super(message, cause);
  }

  public GlobalException(ErrorCode errorCode) {
    super(errorCode);
  }

  public GlobalException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
