package cn.edu.njust.hearth.popquiz_backend.requestBody;

import jakarta.validation.constraints.NotNull;

public class CreateQueCommentRequest {
    @NotNull
    public int question_id;
    @NotNull
    public int user_id;
    @NotNull
    public String comment;

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
