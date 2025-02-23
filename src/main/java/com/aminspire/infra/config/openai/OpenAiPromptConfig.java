package com.aminspire.infra.config.openai;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class OpenAiPromptConfig {
    @Value("${openai.prompts.keyword-extraction}")
    private String keywordExtractionPrompt;
    @Value("${openai.prompts.role}")
    private String role;
    @Value("${openai.prompts.max-tokens}")
    private int maxTokens;
    @Value("${openai.prompts.temperature}")
    private float temperature;
}

