package com.aminspire.global.exception;

import com.aminspire.global.exception.errorcode.BaseErrorCode;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException{

    private BaseErrorCode errorCode;

    public CommonException(BaseErrorCode errorCode){
        this.errorCode = errorCode;
    }

}
