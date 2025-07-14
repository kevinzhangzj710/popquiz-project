package cn.edu.njust.hearth.popquiz_backend.requestBody;

public class CreateSpeechRequest {
    String title;
    int speaker_id;
    int course_id;

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int speaker_id() {
        return speaker_id;
    }

    public void setSpeaker_id(int speaker_id) {
        this.speaker_id = speaker_id;
    }

    public int course_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }
}
