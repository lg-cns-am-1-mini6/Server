package com.aminspire.global.exception;

import com.aminspire.global.exception.errorcode.BaseErrorCode;
import com.aminspire.global.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<CommonResponse<ErrorMsg>> handleCommonException(CommonException e){
        final BaseErrorCode errorCode = e.getErrorCode();
        final CommonResponse<ErrorMsg> response =
                CommonResponse.onFailure(errorCode.getHttpStatus().value(), errorCode.getErrorMsg());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}
