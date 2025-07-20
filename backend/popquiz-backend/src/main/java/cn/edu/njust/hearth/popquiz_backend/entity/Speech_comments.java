package cn.edu.njust.hearth.popquiz_backend.entity;

import jakarta.validation.constraints.NotNull;

public class Speech_comments {
    @NotNull
    public int id;
    @NotNull
    public String comment;
    @NotNull
    public int speech_id;
    @NotNull
    public int user_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getSpeech_id() {
        return speech_id;
    }

    public void setSpeech_id(int speech_id) {
        this.speech_id = speech_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
