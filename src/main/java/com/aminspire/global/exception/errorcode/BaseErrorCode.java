package com.aminspire.global.exception.errorcode;

import com.aminspire.global.exception.ErrorMsg;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus getHttpStatus();

    String getMessage();

    String getCodeName();

    default ErrorMsg getErrorMsg() {
        return ErrorMsg.builder().code(getCodeName()).reason(getMessage()).build();
    }
}
