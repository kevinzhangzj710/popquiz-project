package cn.edu.njust.hearth.popquiz_backend.controller;
import cn.edu.njust.hearth.popquiz_backend.entity.User;
import cn.edu.njust.hearth.popquiz_backend.mapper.UserMapper;
import cn.edu.njust.hearth.popquiz_backend.requestBody.LoginRequest;
import cn.edu.njust.hearth.popquiz_backend.requestBody.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
@RestController
@RequestMapping("/api")
public class LoginController {
    @Autowired
    private UserMapper userMapper;
    @PostMapping("/login")
    @Operation(summary = "用户登录",description = "登录成功返回用户ID，失败返回-1。")
    public int Login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();
        List<User> users = userMapper.findByUsernameAndPassword(username, password);
        if (!users.isEmpty())
            return users.get(0).id();
        else return -1;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册",description = "成功与否没有任何返回值")
    public void register(@RequestBody RegisterRequest  registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(registerRequest.password());
        user.setName(registerRequest.name());
        userMapper.register(user);
    }

}
