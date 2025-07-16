package cn.edu.njust.hearth.popquiz_backend.entity;

import jakarta.validation.constraints.NotNull;

public class Course {

    @NotNull
    public int id;
    @NotNull
    public String title;
    @NotNull
    public String description;
    @NotNull
    public int organizer_id;

    public int id() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String description() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int organizer_id() {
        return organizer_id;
    }

    public void setOrganizer_id(int organizer_id) {
        this.organizer_id = organizer_id;
    }


}
