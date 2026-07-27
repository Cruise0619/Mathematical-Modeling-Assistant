package com.shumo.assistant.service;

import com.shumo.assistant.entity.Conversation;
import com.shumo.assistant.entity.UserSession;
import com.shumo.assistant.repository.ConversationRepository;
import com.shumo.assistant.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private static final String SYSTEM_PROMPT =
        "You are a professional mathematical modeling tutor, skilled at analyzing various math modeling problems and providing professional advice.\n\n" +
        "When users submit math modeling problems, please analyze from the following dimensions:\n\n" +
        "1. **Problem Analysis**: Understand the background, objectives, constraints, and key issues of the problem\n" +
        "2. **Model Suggestions**: Recommend suitable mathematical models and algorithms (e.g., linear programming, nonlinear programming, integer programming, graph theory models, probability models, statistical models, machine learning models, etc.)\n" +
        "3. **Feasibility Analysis**: Evaluate the implementation difficulty, computational complexity, and data requirements\n" +
        "4. **Software Recommendations**: Recommend suitable software tools (MATLAB, Python, R, SPSS, LINGO, CPLEX, etc.)\n" +
        "5. **Data Screening**: Analyze what types of data are needed and how to preprocess the data\n\n" +
        "Please output the analysis results in structured Markdown format, and answer in Chinese.";

    private final ConversationRepository conversationRepository;
    private final UserSessionRepository userSessionRepository;
    private final AiServiceFactory aiServiceFactory;

    public String getOrCreateSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        Optional<UserSession> existing = userSessionRepository.findBySessionId(sessionId);
        if (existing.isEmpty()) {
            UserSession newSession = new UserSession();
            newSession.setSessionId(sessionId);
            userSessionRepository.save(newSession);
        }

        return sessionId;
    }

    public List<Conversation> getHistory(String sessionId) {
        return conversationRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    @Transactional
    public Conversation sendMessage(String sessionId, String userInput, String imageData, String wordContent, String apiKey, String provider) {
        Conversation conversation = new Conversation();
        conversation.setSessionId(sessionId);
        conversation.setUserInput(userInput);
        conversation.setImageData(imageData);
        conversation.setWordContent(wordContent);
        conversationRepository.save(conversation);

        String userMessage = buildUserMessage(userInput, imageData, wordContent);
        AiService aiService = aiServiceFactory.getService(provider);
        String aiResponse = aiService.chat(apiKey, SYSTEM_PROMPT, userMessage);

        conversation.setAiResponse(aiResponse);
        conversationRepository.save(conversation);

        userSessionRepository.findBySessionId(sessionId).ifPresent(session -> {
            session.setApiKey(apiKey);
            session.setProvider(provider);
            userSessionRepository.save(session);
        });

        return conversation;
    }

    private String buildUserMessage(String userInput, String imageData, String wordContent) {
        StringBuilder sb = new StringBuilder();

        if (wordContent != null && !wordContent.isEmpty()) {
            sb.append("[Word Document]\n").append(wordContent).append("\n\n");
        }

        if (userInput != null && !userInput.isEmpty()) {
            sb.append("[User Input]\n").append(userInput).append("\n\n");
        }

        if (imageData != null && !imageData.isEmpty()) {
            sb.append("[User uploaded an image]");
        }

        return sb.toString().trim();
    }

    public Optional<String> getApiKey(String sessionId) {
        return userSessionRepository.findBySessionId(sessionId)
                .map(UserSession::getApiKey);
    }

    public Optional<String> getProvider(String sessionId) {
        return userSessionRepository.findBySessionId(sessionId)
                .map(UserSession::getProvider);
    }

    @Transactional
    public void clearHistory(String sessionId) {
        List<Conversation> history = conversationRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        conversationRepository.deleteAll(history);
    }
}
