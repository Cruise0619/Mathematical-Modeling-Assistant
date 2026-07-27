package com.shumo.assistant.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AiServiceFactory {

    private final Map<String, AiService> services = new HashMap<>();

    public AiServiceFactory(List<AiService> aiServices) {
        for (AiService service : aiServices) {
            services.put(service.getProviderName().toLowerCase(), service);
        }
    }

    public AiService getService(String provider) {
        if (provider == null || provider.isEmpty()) {
            return services.get("minimax");
        }
        return services.getOrDefault(provider.toLowerCase(), services.get("minimax"));
    }

    public String getDefaultProvider() {
        return "minimax";
    }
}
