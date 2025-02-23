package com.aminspire.global.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class OpenAiUtil {

    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.api.uri}")
    private String openaiUri;
    public HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.add("Content-Type", "application/json");
        return headers;
    }
}
