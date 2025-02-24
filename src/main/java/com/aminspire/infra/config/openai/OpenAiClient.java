package com.aminspire.infra.config.openai;


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

    public List<String> generateKeywordExtractPrompt(String text) {

        String prompt = openAiPromptProperties.getKeywordExtractionPrompt() + text;
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4",
                "messages", List.of(
                        Map.of("role", "system", "content", openAiPromptProperties.getRole()),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", openAiPromptProperties.getMaxTokens(),
                "temperature", openAiPromptProperties.getTemperature()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiPromptProperties.getOpenAiUrl());

        //요청 객체 생성
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // OpenAI API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    openAiPromptProperties.getOpenAiUrl(), HttpMethod.POST, requestEntity, Map.class
            );

            //응답 데이터 추출
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

                if (choices != null && !choices.isEmpty()) {
                    //OpenAI 응답에서 키워드 추출
                    String content = (String) choices.get(0).get("message");
                    return List.of(content.split(",")); // ✅ 키워드 리스트 반환
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return List.of();
    }


}
