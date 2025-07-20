package cn.edu.njust.hearth.popquiz_backend;

import cn.edu.njust.hearth.popquiz_backend.controller.CourseController;
import cn.edu.njust.hearth.popquiz_backend.entity.Course;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech;
import cn.edu.njust.hearth.popquiz_backend.mapper.CourseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * 课程控制器测试类，验证课程查询相关功能
 */
class CourseControllerTest {

    // 使用Mockito创建CourseMapper接口的模拟对象
    @Mock
    private CourseMapper courseMapper;

    // 注入模拟对象到CourseController中
    @InjectMocks
    private CourseController courseController;

    // 每个测试方法执行前初始化Mockito环境
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 测试获取用户听力课程列表的成功场景
     */
    @Test
    void testGetListeningCourse() {
        // 测试数据：用户ID和课程ID列表
        Integer uid = 1;
        List<Integer> courseIds = Arrays.asList(1, 2);

        // 创建模拟课程对象
        Course course1 = new Course();
        course1.setId(1);
        course1.setTitle("听力课程1");
        course1.setDescription("基础听力训练");

        Course course2 = new Course();
        course2.setId(2);
        course2.setTitle("听力课程2");
        course2.setDescription("进阶听力训练");

        // 设置Mock对象的行为：当查询用户听力课程ID时返回预设ID列表
        //when(courseMapper.findListenByUid(uid)).thenReturn(courseIds);
        // 设置Mock对象的行为：当查询具体课程信息时返回预设课程对象
        when(courseMapper.findByCid(1)).thenReturn(course1);
        when(courseMapper.findByCid(2)).thenReturn(course2);

        // 执行测试：调用控制器方法获取课程列表
        List<Course> result = courseController.getListeningCourse(uid);

        // 验证结果：检查返回课程数量和内容是否正确
        assertEquals(2, result.size());
        assertEquals("听力课程1", result.get(0).title());
        assertEquals("听力课程2", result.get(1).title());

        // 验证Mock对象的方法调用次数和参数
        verify(courseMapper, times(1)).findListenByUid(uid);
        verify(courseMapper, times(1)).findByCid(1);
        verify(courseMapper, times(1)).findByCid(2);
    }

    /**
     * 测试用户没有听力课程的场景
     */
    @Test
    void testGetListeningCourse_Empty() {
        // 测试数据：用户ID
        Integer uid = 1;

        // 设置Mock对象的行为：返回空课程ID列表
        //when(courseMapper.findListenByUid(uid)).thenReturn(Collections.emptyList());

        // 执行测试
        List<Course> result = courseController.getListeningCourse(uid);

        // 验证结果：返回空列表
        assertEquals(0, result.size());

        // 验证：未调用查询具体课程的方法
        verify(courseMapper, times(1)).findListenByUid(uid);
        verify(courseMapper, never()).findByCid(anyInt());
    }

    /**
     * 测试获取用户口语课程列表的成功场景
     */
    @Test
    void testGetSpeechCourse() {
        // 测试数据：用户ID和课程ID列表
        Integer uid = 1;
        List<Integer> courseIds = Arrays.asList(3, 4);

        // 创建模拟课程对象
        Course course3 = new Course();
        course3.setId(3);
        course3.setTitle("口语课程1");
        course3.setDescription("日常对话练习");

        Course course4 = new Course();
        course4.setId(4);
        course4.setTitle("口语课程2");
        course4.setDescription("商务口语训练");

        // 设置Mock对象的行为
        //when(courseMapper.findCoursesByUid(uid)).thenReturn(courseIds);
        when(courseMapper.findByCid(3)).thenReturn(course3);
        when(courseMapper.findByCid(4)).thenReturn(course4);

        // 执行测试
        List<Course> result = courseController.getListeningCourse(uid);

        // 验证结果
        assertEquals(2, result.size());
        assertEquals("口语课程1", result.get(0).title());
        assertEquals("口语课程2", result.get(1).title());

        // 验证方法调用
        verify(courseMapper, times(1)).findCoursesByUid(uid);
        verify(courseMapper, times(1)).findByCid(3);
        verify(courseMapper, times(1)).findByCid(4);
    }
}