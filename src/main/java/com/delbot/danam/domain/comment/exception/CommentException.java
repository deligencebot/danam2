package com.delbot.danam.domain.comment.exception;

import com.delbot.danam.global.common.exception.CustomException;
import com.delbot.danam.global.common.exception.ErrorCode;

public class CommentException extends CustomException {
      //
    public CommentException() {
        super();
    }

    public CommentException(String message) {
        super(message);
    }

    public CommentException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommentException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
