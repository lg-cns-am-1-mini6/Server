package com.aminspire.global.openai;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final RestTemplate restTemplate = new RestTemplate(); // ✅ RestTemplate 인스턴스 생성
    private final OpenAiPromptProperties openAiPromptProperties;

    public Map<String, List<String>> generateBatchKeywordExtractPrompt(List<String> texts) {
        if (texts.isEmpty()) {
            return Map.of(); // ✅ 빈 리스트가 입력되면 빈 Map 반환
        }

        StringBuilder combinedPrompt = new StringBuilder(openAiPromptProperties.getKeywordExtractionPrompt());
        for (int i = 0; i < texts.size(); i++) {
            combinedPrompt.append("\n\n[기사 ").append(i + 1).append("]\n").append(texts.get(i));
        }

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "system", "content", openAiPromptProperties.getRole()),
                        Map.of("role", "user", "content", combinedPrompt.toString())
                ),
                "max_tokens", openAiPromptProperties.getMaxTokens(),
                "temperature", openAiPromptProperties.getTemperature()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiPromptProperties.getApiKey());

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // ✅ OpenAI API 호출 (배치 요청)
            ResponseEntity<Map> response = restTemplate.exchange(
                    openAiPromptProperties.getOpenAiUrl(), HttpMethod.POST, requestEntity, Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

                if (choices != null && !choices.isEmpty()) {
                    // ✅ OpenAI 응답에서 키워드 JSON 파싱
                    String content = (String) choices.get(0).get("message");
                    return parseKeywordResponse(content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Map.of();
    }

    private Map<String, List<String>> parseKeywordResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response, new TypeReference<Map<String, List<String>>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Map.of();
        }
    }
}
