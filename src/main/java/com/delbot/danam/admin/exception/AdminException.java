package com.delbot.danam.admin.exception;

import com.delbot.danam.global.common.exception.CustomException;
import com.delbot.danam.global.common.exception.ErrorCode;

public class AdminException extends CustomException {
  //
  public AdminException() {
    super();
  }

  public AdminException(String message) {
    super(message);
  }

  public AdminException(String message, Throwable cause) {
    super(message, cause);
  }

  public AdminException(ErrorCode errorCode) {
    super(errorCode);
  }

  public AdminException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }  
}
