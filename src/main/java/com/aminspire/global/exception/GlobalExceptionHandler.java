package com.aminspire.global.exception;

import com.aminspire.global.exception.errorcode.BaseErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorMsg> handleCommonException(CommonException e){
        final BaseErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorCode.getErrorMsg());
    }
}
