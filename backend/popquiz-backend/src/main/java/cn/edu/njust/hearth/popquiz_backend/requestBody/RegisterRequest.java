package cn.edu.njust.hearth.popquiz_backend.requestBody;
import io.swagger.v3.oas.annotations.media.Schema;
public class RegisterRequest {
    @Schema(description = "用户名", required = true, example = "admin")
    private String username;
    @Schema(description = "密码", required = true, example = "password123")
    private String password;
    @Schema(description = "用户姓名", required = true, example = "password123")
    private String name;
    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
