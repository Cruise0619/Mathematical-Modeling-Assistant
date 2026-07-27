package com.shumo.assistant.service;

public interface AiService {
    String chat(String apiKey, String systemPrompt, String userMessage);
    String getProviderName();
}
