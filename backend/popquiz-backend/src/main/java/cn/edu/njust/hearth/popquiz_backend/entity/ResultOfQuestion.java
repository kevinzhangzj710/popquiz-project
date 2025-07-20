package cn.edu.njust.hearth.popquiz_backend.entity;

import jakarta.validation.constraints.NotNull;

public class ResultOfQuestion {
    @NotNull
    public float answeredCount;
    @NotNull
    public float unansweredCount;
    @NotNull
    public float correctCount;
    @NotNull
    public float wrongCount;
    @NotNull
    public float totalCount;
    @NotNull
    public float accuracyRate;

    public float getAnsweredCount() {
        return answeredCount;
    }

    public void setAnsweredCount(float answeredCount) {
        this.answeredCount = answeredCount;
    }

    public float getUnansweredCount() {
        return unansweredCount;
    }

    public void setUnansweredCount(float unansweredCount) {
        this.unansweredCount = unansweredCount;
    }

    public float getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(float correctCount) {
        this.correctCount = correctCount;
    }

    public float getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(float wrongCount) {
        this.wrongCount = wrongCount;
    }

    public float getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(float totalCount) {
        this.totalCount = totalCount;
    }

    public float getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(float accuracyRate) {
        this.accuracyRate = accuracyRate;
    }
}
