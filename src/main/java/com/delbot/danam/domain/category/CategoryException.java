package com.delbot.danam.domain.category;

import com.delbot.danam.global.common.exception.CustomException;
import com.delbot.danam.global.common.exception.ErrorCode;

public class CategoryException extends CustomException {
  //
  public CategoryException() {
    super();
  }

  public CategoryException(String message) {
    super(message);
  }

  public CategoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public CategoryException(ErrorCode errorCode) {
    super(errorCode);
  }

  public CategoryException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
