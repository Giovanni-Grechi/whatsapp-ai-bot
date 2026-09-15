package com.giovanni.whatsapp_ai_bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WhatsAppWebhookDto {

    private String object;
    private List<Entry> entry;

    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }
    public List<Entry> getEntry() { return entry; }
    public void setEntry(List<Entry> entry) { this.entry = entry; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry {
        private String id;
        private List<Change> changes;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public List<Change> getChanges() { return changes; }
        public void setChanges(List<Change> changes) { this.changes = changes; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Change {
        private Value value;
        private String field;

        public Value getValue() { return value; }
        public void setValue(Value value) { this.value = value; }
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Value {
        @JsonProperty("messaging_product")
        private String messagingProduct;
        private Metadata metadata;
        private List<Message> messages;

        public String getMessagingProduct() { return messagingProduct; }
        public void setMessagingProduct(String messagingProduct) { this.messagingProduct = messagingProduct; }
        public Metadata getMetadata() { return metadata; }
        public void setMetadata(Metadata metadata) { this.metadata = metadata; }
        public List<Message> getMessages() { return messages; }
        public void setMessages(List<Message> messages) { this.messages = messages; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        @JsonProperty("display_phone_number")
        private String displayPhoneNumber;
        @JsonProperty("phone_number_id")
        private String phoneNumberId;

        public String getDisplayPhoneNumber() { return displayPhoneNumber; }
        public void setDisplayPhoneNumber(String displayPhoneNumber) { this.displayPhoneNumber = displayPhoneNumber; }
        public String getPhoneNumberId() { return phoneNumberId; }
        public void setPhoneNumberId(String phoneNumberId) { this.phoneNumberId = phoneNumberId; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String from;
        private String id;
        private String timestamp;
        private Text text;

        // Método facilitador para pegar o texto direto sem nullPointerException
        public String getTextBody() {
            if (text != null) {
                return text.getBody();
            }
            return null;
        }

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public Text getText() { return text; }
        public void setText(Text text) { this.text = text; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Text {
        private String body;

        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }
}