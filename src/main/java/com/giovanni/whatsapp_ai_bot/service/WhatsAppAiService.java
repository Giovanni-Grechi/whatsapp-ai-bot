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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WhatsAppAiService {

    @Value("${bot.ai.api-key}")
    private String apiKey;

    @Value("${bot.ai.system-prompt}")
    private String systemPrompt;

    @Value("${bot.meta.access-token}")
    private String metaAccessToken;

    @Value("${bot.meta.phone-number-id}")
    private String phoneNumberId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String openAiUrl = "https://api.openai.com/v1/chat/completions";

    @Cacheable(value = "whatsappContext", key = "#phoneNumber")
    public List<ChatMessage> getOrCreateConversationHistory(String phoneNumber) {
        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("system", systemPrompt));
        return history;
    }

    public String processMessage(String phoneNumber, String userMessage) {
        List<ChatMessage> history = getOrCreateConversationHistory(phoneNumber);

        // 1. Adiciona a mensagem do cliente ao histórico
        history.add(new ChatMessage("user", userMessage));

        // 2. Monta a requisição para a OpenAI
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

                // 4. Salva a resposta da IA no histórico
                history.add(new ChatMessage("assistant", aiText));

                // 5. Envia a resposta de volta para o WhatsApp do usuário
                sendWhatsAppMessage(phoneNumber, aiText);

                System.out.println("IA respondeu para " + phoneNumber + ": " + aiText);
                return aiText;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao comunicar com a OpenAI ou WhatsApp.");
        }

        return "Desculpe, ocorreu um erro ao processar sua mensagem.";
    }

    private void sendWhatsAppMessage(String recipientPhoneNumber, String messageText) {
        String url = "https://graph.facebook.com/v21.0/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(metaAccessToken);

        // Monta o corpo da mensagem exigido pela Meta Cloud API
        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", recipientPhoneNumber);
        body.put("type", "text");

        Map<String, String> textMap = new HashMap<>();
        textMap.put("body", messageText);
        body.put("text", textMap);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERRO DETALHADO AO ENVIAR WHATSAPP: " + e.getMessage());
        }
    }
}