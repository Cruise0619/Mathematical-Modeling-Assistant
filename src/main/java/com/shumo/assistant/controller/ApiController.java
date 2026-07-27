package com.shumo.assistant.controller;

import com.shumo.assistant.entity.Conversation;
import com.shumo.assistant.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApiController {

    private final ConversationService conversationService;

    @PostMapping("/session")
    public ResponseEntity<Map<String, String>> createSession(@RequestBody(required = false) Map<String, String> body) {
        String existingSessionId = body != null ? body.get("sessionId") : null;
        String sessionId = conversationService.getOrCreateSession(existingSessionId);

        Map<String, String> response = new HashMap<>();
        response.put("sessionId", sessionId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<Conversation>> getHistory(@PathVariable String sessionId) {
        List<Conversation> history = conversationService.getHistory(sessionId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/chat")
    public ResponseEntity<Conversation> chat(@RequestBody ChatRequest request) {
        if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
            request.setSessionId(conversationService.getOrCreateSession(null));
        }

        Conversation result = conversationService.sendMessage(
                request.getSessionId(),
                request.getUserInput(),
                request.getImageData(),
                request.getWordContent(),
                request.getApiKey()
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/apikey/{sessionId}")
    public ResponseEntity<Map<String, String>> getApiKey(@PathVariable String sessionId) {
        String apiKey = conversationService.getApiKey(sessionId).orElse("");

        Map<String, String> response = new HashMap<>();
        response.put("apiKey", apiKey != null ? apiKey : "");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/history/{sessionId}")
    public ResponseEntity<Map<String, Object>> clearHistory(@PathVariable String sessionId) {
        conversationService.clearHistory(sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "历史记录已清除");

        return ResponseEntity.ok(response);
    }

    public static class ChatRequest {
        private String sessionId;
        private String userInput;
        private String imageData;
        private String wordContent;
        private String apiKey;

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getUserInput() { return userInput; }
        public void setUserInput(String userInput) { this.userInput = userInput; }
        public String getImageData() { return imageData; }
        public void setImageData(String imageData) { this.imageData = imageData; }
        public String getWordContent() { return wordContent; }
        public void setWordContent(String wordContent) { this.wordContent = wordContent; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    }
}
