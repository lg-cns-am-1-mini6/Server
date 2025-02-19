package com.aminspire.global;

import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.ErrorMsg;
import com.aminspire.global.exception.GlobalExceptionHandler;
import com.aminspire.global.exception.errorcode.ExampleErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ExceptionHandlerTest {

    ExampleErrorCode exampleErrorCode = ExampleErrorCode.USER_NOT_FOUND;

    void throwExceptionWithErrorCode(Object o) {
        Optional<Object> mockObject = Optional.ofNullable(o);
        if (!mockObject.isPresent()) {
            throw new CommonException(exampleErrorCode);
        }
    }



    @Test
    @DisplayName("authError Test")
    void commonExceptionWithAuthErrorTest() {
        assertThatThrownBy(() -> throwExceptionWithErrorCode(null))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining(exampleErrorCode.getMessage());
    }

    @Test
    @DisplayName("ExceptionHandler Test")
    void globalExceptionHandlerTest() {

        // given: 예외 핸들러와 예외 생성
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        CommonException exception =
                new CommonException(ExampleErrorCode.USER_NOT_FOUND);

        // when: 예외 핸들러 실행
        ResponseEntity<ErrorMsg> response =
                globalExceptionHandler.handleCommonException(exception);

        // then: 응답 객체 검증
        assertThat(response).isNotNull();
        System.out.println(response);
        assertThat(response.getStatusCode()).isEqualTo(ExampleErrorCode.USER_NOT_FOUND.getHttpStatus());
        assertThat(response.getBody().getCode()).isEqualTo(String.valueOf(ExampleErrorCode.USER_NOT_FOUND));
        assertThat(response.getBody().getReason()).isEqualTo(ExampleErrorCode.USER_NOT_FOUND.getMessage());
    }
}
