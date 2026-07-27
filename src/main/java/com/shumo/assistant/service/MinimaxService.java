package com.shumo.assistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinimaxService implements AiService {

    private static final String API_URL = "https://api.minimax.chat/v1/text/chatcompletion_v2";
    private static final String MODEL = "MiniMax-Text-01";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Override
    public String chat(String apiKey, String systemPrompt, String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        requestBody.put("max_tokens", 4096);
        requestBody.put("system", systemPrompt);

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", userMessage);

        requestBody.put("messages", new Map[]{message});

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
            log.error("Minimax API 调用失败", e);
            String msg = e.getMessage();
            if (msg.contains("401") || msg.contains("403") || msg.contains("invalid")) {
                throw new RuntimeException("API 认证失败：请检查 API Key 是否正确");
            }
            throw new RuntimeException("AI 服务调用失败: " + msg);
        }
    }

    private String parseResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
            return "未获取到有效响应";
        } catch (Exception e) {
            log.error("解析响应失败: {}", response, e);
            throw new RuntimeException("解析 AI 响应失败");
        }
    }

    @Override
    public String getProviderName() {
        return "minimax";
    }
}
