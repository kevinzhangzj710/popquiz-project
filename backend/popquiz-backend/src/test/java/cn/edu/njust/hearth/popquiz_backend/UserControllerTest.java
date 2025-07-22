package cn.edu.njust.hearth.popquiz_backend;

import cn.edu.njust.hearth.popquiz_backend.controller.UserController;
import cn.edu.njust.hearth.popquiz_backend.entity.Course;
import cn.edu.njust.hearth.popquiz_backend.entity.Course_listener;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech;
import cn.edu.njust.hearth.popquiz_backend.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 测试用户是学生的情况
    @Test
    void testGetTypeofUser_Student() {
        int userId = 1;
        int speechId = 1;

        Course_listener courseListener = new Course_listener();
        courseListener.setId(1);
        courseListener.setCourse_id(1);
        courseListener.setUser_id(userId);

        when(userMapper.findCour_Listener(userId, speechId)).thenReturn(courseListener);
        when(userMapper.findCourseById(userId, speechId)).thenReturn(null);
        when(userMapper.findSpeechById(userId, speechId)).thenReturn(null);

        int result = userController.GetTypeofUser(userId, speechId);

        assertEquals(1, result);
        verify(userMapper, times(1)).findCour_Listener(userId, speechId);
        verify(userMapper, times(1)).findCourseById(userId, speechId);
        verify(userMapper, times(1)).findSpeechById(userId, speechId);
    }

    // 测试用户是演讲者的情况
    @Test
    void testGetTypeofUser_Speaker() {
        int userId = 1;
        int speechId = 1;

        Speech speech = new Speech();
        speech.setId(1);
        speech.setTitle("Test Speech");
        speech.setSpeaker_id(userId);
        speech.setCourse_id(1);

        when(userMapper.findCour_Listener(userId, speechId)).thenReturn(null);
        when(userMapper.findCourseById(userId, speechId)).thenReturn(null);
        when(userMapper.findSpeechById(userId, speechId)).thenReturn(speech);

        int result = userController.GetTypeofUser(userId, speechId);

        assertEquals(0, result);
        verify(userMapper, times(1)).findCour_Listener(userId, speechId);
        verify(userMapper, times(1)).findCourseById(userId, speechId);
        verify(userMapper, times(1)).findSpeechById(userId, speechId);
    }

    // 测试用户是组织者的情况
    @Test
    void testGetTypeofUser_Organizer() {
        int userId = 1;
        int speechId = 1;

        Course course = new Course();
        course.setId(1);
        course.setTitle("Test Course");
        course.setOrganizer_id(userId);

        when(userMapper.findCour_Listener(userId, speechId)).thenReturn(null);
        when(userMapper.findCourseById(userId, speechId)).thenReturn(course);
        when(userMapper.findSpeechById(userId, speechId)).thenReturn(null);

        int result = userController.GetTypeofUser(userId, speechId);

        assertEquals(0, result);
        verify(userMapper, times(1)).findCour_Listener(userId, speechId);
        verify(userMapper, times(1)).findCourseById(userId, speechId);
        verify(userMapper, times(1)).findSpeechById(userId, speechId);
    }

    // 测试用户既不是学生也不是演讲者或组织者的情况
    @Test
    void testGetTypeofUser_Neither() {
        int userId = 1;
        int speechId = 1;

        when(userMapper.findCour_Listener(userId, speechId)).thenReturn(null);
        when(userMapper.findCourseById(userId, speechId)).thenReturn(null);
        when(userMapper.findSpeechById(userId, speechId)).thenReturn(null);

        int result = userController.GetTypeofUser(userId, speechId);

        assertEquals(-1, result);
        verify(userMapper, times(1)).findCour_Listener(userId, speechId);
        verify(userMapper, times(1)).findCourseById(userId, speechId);
        verify(userMapper, times(1)).findSpeechById(userId, speechId);
    }

    // 测试用户既是学生又是演讲者的情况（边界情况）
    @Test
    void testGetTypeofUser_StudentAndSpeaker() {
        int userId = 1;
        int speechId = 1;

        Course_listener courseListener = new Course_listener();
        courseListener.setId(1);
        courseListener.setCourse_id(1);
        courseListener.setUser_id(userId);

        Speech speech = new Speech();
        speech.setId(1);
        speech.setTitle("Test Speech");
        speech.setSpeaker_id(userId);
        speech.setCourse_id(1);

        when(userMapper.findCour_Listener(userId, speechId)).thenReturn(courseListener);
        when(userMapper.findCourseById(userId, speechId)).thenReturn(null);
        when(userMapper.findSpeechById(userId, speechId)).thenReturn(speech);

        int result = userController.GetTypeofUser(userId, speechId);

        // 根据业务逻辑，优先判断为学生
        assertEquals(1, result);
        verify(userMapper, times(1)).findCour_Listener(userId, speechId);
        verify(userMapper, times(1)).findCourseById(userId, speechId);
        verify(userMapper, times(1)).findSpeechById(userId, speechId);
    }

    // 测试用户既是学生又是组织者的情况（边界情况）
    @Test
    void testGetTypeofUser_StudentAndOrganizer() {
        int userId = 1;
        int speechId = 1;

        Course_listener courseListener = new Course_listener();
        courseListener.setId(1);
        courseListener.setCourse_id(1);
        courseListener.setUser_id(userId);

        Course course = new Course();
        course.setId(1);
        course.setTitle("Test Course");
        course.setOrganizer_id(userId);

        when(userMapper.findCour_Listener(userId, speechId)).thenReturn(courseListener);
        when(userMapper.findCourseById(userId, speechId)).thenReturn(course);
        when(userMapper.findSpeechById(userId, speechId)).thenReturn(null);

        int result = userController.GetTypeofUser(userId, speechId);

        // 根据业务逻辑，优先判断为学生
        assertEquals(1, result);
        verify(userMapper, times(1)).findCour_Listener(userId, speechId);
        verify(userMapper, times(1)).findCourseById(userId, speechId);
        verify(userMapper, times(1)).findSpeechById(userId, speechId);
    }

    // 测试用户既是演讲者又是组织者的情况（边界情况）
    @Test
    void testGetTypeofUser_SpeakerAndOrganizer() {
        int userId = 1;
        int speechId = 1;

        Speech speech = new Speech();
        speech.setId(1);
        speech.setTitle("Test Speech");
        speech.setSpeaker_id(userId);
        speech.setCourse_id(1);

        Course course = new Course();
        course.setId(1);
        course.setTitle("Test Course");
        course.setOrganizer_id(userId);

        when(userMapper.findCour_Listener(userId, speechId)).thenReturn(null);
        when(userMapper.findCourseById(userId, speechId)).thenReturn(course);
        when(userMapper.findSpeechById(userId, speechId)).thenReturn(speech);

        int result = userController.GetTypeofUser(userId, speechId);

        assertEquals(0, result);
        verify(userMapper, times(1)).findCour_Listener(userId, speechId);
        verify(userMapper, times(1)).findCourseById(userId, speechId);
        verify(userMapper, times(1)).findSpeechById(userId, speechId);
    }

    // 测试用户同时具备三种身份的情况（边界情况）
    @Test
    void testGetTypeofUser_StudentSpeakerOrganizer() {
        int userId = 1;
        int speechId = 1;

        Course_listener courseListener = new Course_listener();
        courseListener.setId(1);
        courseListener.setCourse_id(1);
        courseListener.setUser_id(userId);

        Speech speech = new Speech();
        speech.setId(1);
        speech.setTitle("Test Speech");
        speech.setSpeaker_id(userId);
        speech.setCourse_id(1);

        Course course = new Course();
        course.setId(1);
        course.setTitle("Test Course");
        course.setOrganizer_id(userId);

        when(userMapper.findCour_Listener(userId, speechId)).thenReturn(courseListener);
        when(userMapper.findCourseById(userId, speechId)).thenReturn(course);
        when(userMapper.findSpeechById(userId, speechId)).thenReturn(speech);

        int result = userController.GetTypeofUser(userId, speechId);

        // 根据业务逻辑，优先判断为学生
        assertEquals(1, result);
        verify(userMapper, times(1)).findCour_Listener(userId, speechId);
        verify(userMapper, times(1)).findCourseById(userId, speechId);
        verify(userMapper, times(1)).findSpeechById(userId, speechId);
    }
}