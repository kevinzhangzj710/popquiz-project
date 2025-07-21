package cn.edu.njust.hearth.popquiz_backend.entity;

import jakarta.validation.constraints.NotNull;

public class Speech_files {
    @NotNull
    public int id;
    @NotNull
    public String filename;
    @NotNull
    public String filepath;
    @NotNull
    public int speech_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getSpeech_id() {
        return speech_id;
    }

    public void setSpeech_id(int speech_id) {
        this.speech_id = speech_id;
    }
}
