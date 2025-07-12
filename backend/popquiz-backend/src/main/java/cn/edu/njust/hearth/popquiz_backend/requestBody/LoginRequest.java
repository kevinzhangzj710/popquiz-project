package cn.edu.njust.hearth.popquiz_backend.requestBody;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginRequest {
    @Schema(description = "用户名", required = true, example = "admin")
    private String username;
    @Schema(description = "密码", required = true, example = "password123")
    private String password;
    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
