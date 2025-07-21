package cn.edu.njust.hearth.popquiz_backend.entity;

import jakarta.validation.constraints.NotNull;

public class Speech_content {
    @NotNull
    public int id;
    @NotNull
    public int speech_id;
    @NotNull
    public String content_path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpeech_id() {
        return speech_id;
    }

    public void setSpeech_id(int speech_id) {
        this.speech_id = speech_id;
    }

    public String getContent_path() {
        return content_path;
    }

    public void setContent_path(String content_path) {
        this.content_path = content_path;
    }
}
