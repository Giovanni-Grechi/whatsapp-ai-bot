package com.giovanni.whatsapp_ai_bot.dto;

import com.giovanni.whatsapp_ai_bot.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OpenAiRequest {
    private String model;
    private List<ChatMessage> messages;
}