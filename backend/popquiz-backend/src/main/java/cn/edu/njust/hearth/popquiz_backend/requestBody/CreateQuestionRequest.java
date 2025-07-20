package cn.edu.njust.hearth.popquiz_backend.requestBody;

import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

public class CreateQuestionRequest {
    @NotNull
    public String question;
    @NotNull
    public String selection;
    @NotNull
    public char answer;
    @NotNull
    public int speech_id;
    @NotNull
    public Timestamp start_time;
    @NotNull
    public Timestamp end_time;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public char getAnswer() {
        return answer;
    }

    public void setAnswer(char answer) {
        this.answer = answer;
    }

    public int getSpeech_id() {
        return speech_id;
    }

    public void setSpeech_id(int speech_id) {
        this.speech_id = speech_id;
    }

    public Timestamp getStart_time() {
        return start_time;
    }

    public void setStart_time(Timestamp start_time) {
        this.start_time = start_time;
    }

    public Timestamp getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Timestamp end_time) {
        this.end_time = end_time;
    }
}
