package com.aminspire.infra.config.openai;

package com.example.ai.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public List<String> callOpenAiApi(String text) {
        String prompt = "다음 텍스트에서 핵심 키워드를 5개 추출하여 JSON 배열로 반환해줘:\n\n" + text;

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4",
                "messages", List.of(
                        Map.of("role", "system", "content", "너는 한국어 텍스트에서 주요 키워드를 추출하는 도우미야."),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 100,
                "temperature", 0.5
        );

        return webClient.post()
                .uri(OPENAI_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<Map<String, String>> choices = (List<Map<String, String>>) response.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        return List.of(choices.get(0).get("message").get("content").split(","));
                    }
                    return List.of();
                })
                .block();
    }
}
