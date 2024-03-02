package com.delbot.danam.domain.post.exception;

import com.delbot.danam.global.common.exception.CustomException;
import com.delbot.danam.global.common.exception.ErrorCode;

public class PostException extends CustomException {
  //
  public PostException() {
    super();
  }

  public PostException(String message) {
    super(message);
  }

  public PostException(String message, Throwable cause) {
    super(message, cause);
  }

  public PostException(ErrorCode errorCode) {
    super(errorCode);
  }

  public PostException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
