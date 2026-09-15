package com.giovanni.whatsapp_ai_bot.dto;

import com.giovanni.whatsapp_ai_bot.model.ChatMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenAiResponse {
    private List<Choice> choices;

    @Getter
    @Setter
    public static class Choice {
        private int index;
        private ChatMessage message;
    }
}