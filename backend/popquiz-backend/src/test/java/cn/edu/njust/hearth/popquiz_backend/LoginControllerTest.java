package cn.edu.njust.hearth.popquiz_backend;

import cn.edu.njust.hearth.popquiz_backend.controller.LoginController;
import cn.edu.njust.hearth.popquiz_backend.mapper.UserMapper;
import cn.edu.njust.hearth.popquiz_backend.requestBody.RegisterRequest;
import cn.edu.njust.hearth.popquiz_backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import cn.edu.njust.hearth.popquiz_backend.controller.LoginController;
import cn.edu.njust.hearth.popquiz_backend.mapper.UserMapper;
import cn.edu.njust.hearth.popquiz_backend.requestBody.LoginRequest;
import cn.edu.njust.hearth.popquiz_backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import cn.edu.njust.hearth.popquiz_backend.controller.LoginController;
import cn.edu.njust.hearth.popquiz_backend.entity.User;
import cn.edu.njust.hearth.popquiz_backend.mapper.UserMapper;
import cn.edu.njust.hearth.popquiz_backend.requestBody.LoginRequest;
class LoginControllerTest {

    // 使用 @Mock 注解创建 UserMapper 的模拟对象
    @Mock
    private UserMapper userMapper;

    // 使用 @InjectMocks 注解将模拟的 UserMapper 注入到 LoginController 中
    @InjectMocks
    private LoginController loginController;

    // 在每个测试方法执行前进行初始化操作
    @BeforeEach
    void setUp() {
        // 初始化 Mockito 注解，使得 @Mock 和 @InjectMocks 生效
        MockitoAnnotations.openMocks(this);
    }

    // 测试注册功能，验证能否注册成功
    @Test
    void testRegisterSuccess() {
        // 创建一个 RegisterRequest 对象，并设置用户名、密码和用户姓名
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newUser");
        registerRequest.setPassword("newPassword");
        registerRequest.setName("New User");

        // 创建一个 User 对象，并设置用户信息
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("newPassword");
        user.setName("New User");

        // 调用 LoginController 的 register 方法进行注册操作
        loginController.register(registerRequest);

        // 使用 Mockito 的 verify 方法验证 userMapper 的 register 方法是否被调用了一次，
        // 且传入的参数为正确的 User 对象
        verify(userMapper, times(1)).register(argThat(u ->
                u.username().equals("newUser") &&
                        u.password().equals("newPassword") &&
                        u.name().equals("New User")
        ));
    }

    @Test
    void testLoginFailureRevamped() {
        // 创建一个 LoginRequest 对象，并设置用户名和错误的密码
        LoginRequest loginRequest = new LoginRequest();
        String wrongUsername = "nonexistentUser";
        String wrongPassword = "wrongPassword";
        loginRequest.setUsername(wrongUsername);
        loginRequest.setPassword(wrongPassword);

        // 使用 Mockito 的 when 方法模拟 userMapper 的 findByUsernameAndPassword 方法，
        // 当传入不存在的用户名和错误的密码时，返回一个空列表
        when(userMapper.findByUsernameAndPassword(wrongUsername, wrongPassword)).thenReturn(new ArrayList<>());

        // 调用 LoginController 的 Login 方法进行登录操作，并获取返回结果
        int result = loginController.Login(loginRequest);

        // 使用 JUnit 5 的 assertEquals 方法验证返回结果是否为 -1
        assertEquals(-1, result);

        // 使用 Mockito 的 verify 方法验证 userMapper 的 findByUsernameAndPassword 方法是否被调用了一次
        verify(userMapper, times(1)).findByUsernameAndPassword(wrongUsername, wrongPassword);
    }

    // 测试登录成功的情况
    @Test
    void testLoginSuccess() {
        // 1. 创建一个 LoginRequest 对象，并设置正确的用户名和密码
        LoginRequest loginRequest = new LoginRequest();
        String validUsername = "testUser";
        String validPassword = "testPassword";
        loginRequest.setUsername(validUsername);
        loginRequest.setPassword(validPassword);

        // 2. 创建一个 User 对象，并设置用户信息
        User user = new User();
        user.setUsername(validUsername);
        user.setPassword(validPassword);
        int userId = 1; // 假设用户 ID 为 1
        user.setId(userId);

        // 3. 创建一个用户列表，并将用户对象添加到列表中
        List<User> users = new ArrayList<>();
        users.add(user);

        // 4. 使用 Mockito 的 when 方法模拟 userMapper 的 findByUsernameAndPassword 方法，
        // 当传入正确的用户名和密码时，返回包含该用户的列表
        when(userMapper.findByUsernameAndPassword(validUsername, validPassword)).thenReturn(users);

        // 5. 调用 LoginController 的 Login 方法进行登录操作，并获取返回结果
        int result = loginController.Login(loginRequest);

        // 6. 使用 JUnit 5 的 assertEquals 方法验证返回结果是否为用户 ID
        assertEquals(userId, result);

        // 7. 使用 Mockito 的 verify 方法验证 userMapper 的 findByUsernameAndPassword 方法是否被调用了一次
        verify(userMapper, times(1)).findByUsernameAndPassword(validUsername, validPassword);
    }


}