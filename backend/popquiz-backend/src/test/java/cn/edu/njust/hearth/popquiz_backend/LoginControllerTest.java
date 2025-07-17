package cn.edu.njust.hearth.popquiz_backend;

// 导入 LoginController 类，用于测试该类的方法
import cn.edu.njust.hearth.popquiz_backend.controller.LoginController;
// 导入 UserMapper 接口，用于模拟该接口的行为
import cn.edu.njust.hearth.popquiz_backend.mapper.UserMapper;
// 导入 LoginRequest 类，用于创建登录请求对象
import cn.edu.njust.hearth.popquiz_backend.requestBody.LoginRequest;
// 导入 RegisterRequest 类，用于创建注册请求对象
import cn.edu.njust.hearth.popquiz_backend.requestBody.RegisterRequest;
// 导入 User 类，用于创建用户对象
import cn.edu.njust.hearth.popquiz_backend.entity.User;
// 导入 JUnit 5 的 BeforeEach 注解，用于在每个测试方法执行前执行初始化操作
import org.junit.jupiter.api.BeforeEach;
// 导入 JUnit 5 的 Test 注解，用于标记测试方法
import org.junit.jupiter.api.Test;
// 导入 Mockito 的 InjectMocks 注解，用于注入模拟对象到被测试对象中
import org.mockito.InjectMocks;
// 导入 Mockito 的 Mock 注解，用于创建模拟对象
import org.mockito.Mock;
// 导入 Mockito 的 MockitoAnnotations 类，用于初始化 Mockito 注解
import org.mockito.MockitoAnnotations;
// 导入 ArrayList 类，用于创建用户列表
import java.util.ArrayList;
// 导入 List 接口，用于存储用户列表
import java.util.List;
// 导入 JUnit 5 的 Assertions 类，用于进行断言操作
import static org.junit.jupiter.api.Assertions.assertEquals;
// 导入 Mockito 的 Mockito 类，用于进行模拟对象的验证操作
import static org.mockito.Mockito.*;

// 定义 LoginController 的测试类
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

    // 测试登录成功的情况
    @Test
    void testLoginSuccess() {
        // 创建一个 LoginRequest 对象，并设置用户名和密码
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassword");

        // 创建一个 User 对象，并设置用户 ID、用户名和密码
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        // 创建一个用户列表，并将用户对象添加到列表中
        List<User> users = new ArrayList<>();
        users.add(user);

        // 使用 Mockito 的 when 方法模拟 userMapper 的 findByUsernameAndPassword 方法，
        // 当传入 "testUser" 和 "testPassword" 时，返回包含该用户的列表
        when(userMapper.findByUsernameAndPassword("testUser", "testPassword")).thenReturn(users);

        // 调用 LoginController 的 Login 方法进行登录操作，并获取返回结果
        int result = loginController.Login(loginRequest);

        // 使用 JUnit 5 的 assertEquals 方法验证返回结果是否为 1
        assertEquals(1, result);
        // 使用 Mockito 的 verify 方法验证 userMapper 的 findByUsernameAndPassword 方法是否被调用了一次
        verify(userMapper, times(1)).findByUsernameAndPassword("testUser", "testPassword");
    }

    // 测试登录失败的情况
    @Test
    void testLoginFailure() {
        // 创建一个 LoginRequest 对象，并设置用户名和错误的密码
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("wrongPassword");

        // 使用 Mockito 的 when 方法模拟 userMapper 的 findByUsernameAndPassword 方法，
        // 当传入 "testUser" 和 "wrongPassword" 时，返回一个空列表
        when(userMapper.findByUsernameAndPassword("testUser", "wrongPassword")).thenReturn(new ArrayList<>());

        // 调用 LoginController 的 Login 方法进行登录操作，并获取返回结果
        int result = loginController.Login(loginRequest);

        // 使用 JUnit 5 的 assertEquals 方法验证返回结果是否为 -1
        assertEquals(-1, result);
        // 使用 Mockito 的 verify 方法验证 userMapper 的 findByUsernameAndPassword 方法是否被调用了一次
        verify(userMapper, times(1)).findByUsernameAndPassword("testUser", "wrongPassword");
    }

    // 测试注册功能
    @Test
    void testRegister() {
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
}