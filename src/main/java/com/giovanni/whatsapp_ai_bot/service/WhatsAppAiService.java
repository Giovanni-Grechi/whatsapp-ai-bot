package com.giovanni.whatsapp_ai_bot.service;

import com.giovanni.whatsapp_ai_bot.dto.OpenAiRequest;
import com.giovanni.whatsapp_ai_bot.dto.OpenAiResponse;
import com.giovanni.whatsapp_ai_bot.model.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class WhatsAppAiService {

    @Value("${bot.ai.api-key}")
    private String apiKey;

    @Value("${bot.ai.system-prompt}")
    private String systemPrompt;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String openAiUrl = "https://api.openai.com/v1/chat/completions";

    @Cacheable(value = "whatsappContext", key = "#phoneNumber")
    public List<ChatMessage> getOrCreateConversationHistory(String phoneNumber) {
        List<ChatMessage> history = new ArrayList<>();
        // Injeta a regra de negócio (comportamento da IA) apenas na criação do cache
        history.add(new ChatMessage("system", systemPrompt));
        return history;
    }

    public String processMessage(String phoneNumber, String userMessage) {
        List<ChatMessage> history = getOrCreateConversationHistory(phoneNumber);

        // 1. Adiciona a mensagem do cliente
        history.add(new ChatMessage("user", userMessage));

        // 2. Monta a requisição HTTP
        OpenAiRequest request = new OpenAiRequest("gpt-4o-mini", history);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);

        try {
            // 3. Dispara para a OpenAI
            OpenAiResponse response = restTemplate.postForObject(openAiUrl, entity, OpenAiResponse.class);

            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String aiText = response.getChoices().get(0).getMessage().getContent();

                // 4. Salva a resposta da IA no histórico para manter o contexto das próximas mensagens
                history.add(new ChatMessage("assistant", aiText));

                System.out.println("IA respondeu para " + phoneNumber + ": " + aiText);
                return aiText;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao comunicar com a OpenAI.");
        }

        return "Desculpe, ocorreu um erro ao processar sua mensagem.";
    }
}