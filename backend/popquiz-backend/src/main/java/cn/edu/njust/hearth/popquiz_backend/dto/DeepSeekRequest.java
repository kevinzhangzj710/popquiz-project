package cn.edu.njust.hearth.popquiz_backend.dto;

import java.util.List;

public class DeepSeekRequest {
    private String model;
    private List<Message> messages;
    private double temperature;
    private int max_tokens;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMax_tokens() {
        return max_tokens;
    }

    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }

    // 构造器、getters、setters
    public static class Message {
        private String role;
        private String content;

        public Message(String user, String prompt) {
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
// getters & setters
    }
}