package com.giovanni.whatsapp_ai_bot.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class WhatsAppWebhookDto {
    private List<Entry> entry;

    @Getter
    @Setter
    public static class Entry {
        private List<Change> changes;
    }

    @Getter
    @Setter
    public static class Change {
        private Value value;
    }

    @Getter
    @Setter
    public static class Value {
        private List<Message> messages;
        private List<Contact> contacts;
    }

    @Getter
    @Setter
    public static class Message {
        private String from;
        private String textBody;
    }

    @Getter
    @Setter
    public static class Contact {
        private Profile profile;
    }

    @Getter
    @Setter
    public static class Profile {
        private String name;
    }
}