package com.aminspire.domain.openai.service;

import com.aminspire.domain.openai.dto.AiKeywordsExtractResponse;
import com.aminspire.infra.config.openai.OpenAiPromptConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiService {

    private final OpenAiPromptConfig promptConfig;

    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.api.uri}")
    private String openaiUri;





    public AiKeywordsExtractResponse generateKeywordExtractPrompt(String text) {

        String prompt = promptConfig.getKeywordExtractionPrompt() + text;
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4",
                "messages", List.of(
                        Map.of("role", "system", "content", promptConfig.getRole()),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", promptConfig.getMaxTokens(),
                "temperature", promptConfig.getTemperature()
        );

        return "";
//                .uri(openaiUri)
//                .header("Authorization", "Bearer " + apiKey)
//                .header("Content-Type", "application/json")
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(Map.class)
//                .map(response -> {
//                    List<Map<String, String>> choices = (List<Map<String, String>>) response.get("choices");
//                    if (choices != null && !choices.isEmpty()) {
//                        return List.of(choices.get(0).get("message").get("content").split(","));
//                    }
//                    return List.of();
//                })
//                .block();
    }

    private List<String> parseKeywordsFromResponse(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode keywordsNode = jsonNode.path("keywords");

            if (keywordsNode.isArray()) {
                return objectMapper.convertValue(keywordsNode, List.class);
            }
        } catch (Exception e) {
            log.error("JSON 파싱 오류: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

}