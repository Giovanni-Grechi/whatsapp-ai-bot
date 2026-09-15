package com.giovanni.whatsapp_ai_bot.controller;

import com.giovanni.whatsapp_ai_bot.dto.WhatsAppWebhookDto;
import com.giovanni.whatsapp_ai_bot.service.WhatsAppAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final WhatsAppAiService whatsappAiService;

    public WebhookController(WhatsAppAiService whatsappAiService) {
        this.whatsappAiService = whatsappAiService;
    }

    // Endpoint GET para validação do Webhook (exigido pela Meta/APIs de WhatsApp)
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String verifyToken,
            @RequestParam("hub.challenge") String challenge) {

        // Defina aqui um token de segurança simples para testes
        String myVerifyToken = "token_securo_123";

        if ("subscribe".equals(mode) && myVerifyToken.equals(verifyToken)) {
            return ResponseEntity.ok(challenge);
        }

        return ResponseEntity.status(403).body("Falha na verificação do token");
    }

    // Endpoint POST que recebe as mensagens enviadas pelos clientes
    @PostMapping
    public ResponseEntity<Void> receiveMessage(@RequestBody WhatsAppWebhookDto payload) {
        try {
            // Navega pelo DTO para extrair o telefone e o texto da mensagem
            if (payload.getEntry() != null && !payload.getEntry().isEmpty()) {
                for (WhatsAppWebhookDto.Entry entry : payload.getEntry()) {
                    for (WhatsAppWebhookDto.Change change : entry.getChanges()) {
                        if (change.getValue() != null && change.getValue().getMessages() != null) {
                            for (WhatsAppWebhookDto.Message msg : change.getValue().getMessages()) {
                                String phoneNumber = msg.getFrom();
                                String textBody = msg.getTextBody();

                                if (phoneNumber != null && textBody != null) {
                                    // Processa a mensagem usando o serviço com cache
                                    whatsappAiService.processMessage(phoneNumber, textBody);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Loga o erro se necessário, mas sempre retorna 200 para o WhatsApp não ficar reenvidando
            e.printStackTrace();
        }

        // Retorna 200 OK imediatamente para evitar timeout na plataforma de mensagens
        return ResponseEntity.ok().build();
    }
}