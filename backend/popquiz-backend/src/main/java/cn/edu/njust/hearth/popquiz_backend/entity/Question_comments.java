package cn.edu.njust.hearth.popquiz_backend.entity;

import jakarta.validation.constraints.NotNull;

public class Question_comments {
    @NotNull
    public int id;
    @NotNull
    public int question_id;
    @NotNull
    public int user_id;
    @NotNull
    public String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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


}
