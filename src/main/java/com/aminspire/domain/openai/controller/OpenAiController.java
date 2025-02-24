package com.aminspire.domain.openai.controller;

import com.aminspire.domain.openai.dto.AiKeywordsExtractResponse;
import com.aminspire.domain.openai.service.OpenAiService;
import com.aminspire.global.util.OpenAiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class OpenAiController {

    private final OpenAiService openAiService;
    private final OpenAiUtil openAiUtil;

    @PostMapping("/keywords")
    public AiKeywordsExtractResponse keywordsExtract(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        AiKeywordsExtractResponse response= openAiService.generateKeywordExtractPrompt(text);
        HttpHeaders headers  = openAiUtil.getHeaders();
        return ResponseEntity.ok().headers(headers).body(response);
    }
}
