package com.shumo.assistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepseekService implements AiService {

    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String MODEL = "deepseek-chat";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Override
    public String chat(String apiKey, String systemPrompt, String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        requestBody.put("max_tokens", 4096);

        List<Map<String, String>> messages = new java.util.ArrayList<>();

        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);

        messages.add(systemMsg);
        messages.add(userMsg);
        requestBody.put("messages", messages);

        try {
            String response = webClient.post()
                    .uri(API_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();

            return parseResponse(response);
        } catch (Exception e) {
            log.error("Deepseek API call failed", e);
            String msg = e.getMessage();
            // Try to extract error message from response
            if (msg.contains("401") || msg.contains("403") || msg.contains("invalid")) {
                throw new RuntimeException("API 认证失败：请检查 API Key 是否正确");
            }
            throw new RuntimeException("AI 服务调用失败: " + msg);
        }
    }

    @Override
    public String getProviderName() {
        return "deepseek";
    }

    private String parseResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
            return "No valid response";
        } catch (Exception e) {
            log.error("Failed to parse response: {}", response, e);
            throw new RuntimeException("Failed to parse AI response");
        }
    }
}
