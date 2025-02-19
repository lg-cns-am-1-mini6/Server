package com.aminspire.global.exception.errorcode;

import com.aminspire.global.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @GetMapping("/")
    ResponseEntity<Void> home(){
        if(10==0){
            return ResponseEntity.ok().build();
        }else{
            throw new CommonException(ExampleErrorCode.USER_NOT_FOUND);
        }
    }
}
