package cn.edu.njust.hearth.popquiz_backend.requestBody;

public class DeleteCourseRequest {
    public int uid;
    public int course_id;

    public int uid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int course_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }
}
