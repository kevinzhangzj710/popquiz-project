package cn.edu.njust.hearth.popquiz_backend.entity;

public class User {


    public int id;
    public String username;
    public String password;
    public String name;

    public int id() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
