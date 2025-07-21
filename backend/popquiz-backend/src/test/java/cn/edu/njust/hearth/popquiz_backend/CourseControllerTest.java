package cn.edu.njust.hearth.popquiz_backend;

import cn.edu.njust.hearth.popquiz_backend.controller.CourseController;
import cn.edu.njust.hearth.popquiz_backend.entity.Course;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech;
import cn.edu.njust.hearth.popquiz_backend.mapper.CourseMapper;
import cn.edu.njust.hearth.popquiz_backend.requestBody.AddCourseRequest;
import cn.edu.njust.hearth.popquiz_backend.requestBody.CreateSpeechRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import cn.edu.njust.hearth.popquiz_backend.controller.CourseController;
import cn.edu.njust.hearth.popquiz_backend.entity.Course;
import cn.edu.njust.hearth.popquiz_backend.mapper.CourseMapper;
import cn.edu.njust.hearth.popquiz_backend.requestBody.CreateCourseRequest;
import cn.edu.njust.hearth.popquiz_backend.requestBody.DeleteCourseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.StringUtils;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class CourseControllerTest {

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 测试获取用户所听课程列表 - 正常情况
     */
    @Test
    void testGetListeningCourse_NormalCase() {
        Integer uid = 1;
        Set<Integer> courseIds = new HashSet<>(Arrays.asList(1, 2));

        Course course1 = new Course();
        course1.setId(1);
        course1.setTitle("听力课程1");
        course1.setDescription("基础听力训练");
        course1.setOrganizer_id(1);

        Course course2 = new Course();
        course2.setId(2);
        course2.setTitle("听力课程2");
        course2.setDescription("进阶听力训练");
        course2.setOrganizer_id(2);

        when(courseMapper.findListenByUid(uid)).thenReturn(courseIds);
        when(courseMapper.findByCid(1)).thenReturn(course1);
        when(courseMapper.findByCid(2)).thenReturn(course2);

        List<Course> result = courseController.getListeningCourse(uid);

        assertEquals(2, result.size());
        assertEquals("听力课程1", result.get(0).title());
        assertEquals("听力课程2", result.get(1).title());

        verify(courseMapper, times(1)).findListenByUid(uid);
        verify(courseMapper, times(1)).findByCid(1);
        verify(courseMapper, times(1)).findByCid(2);
    }

    /**
     * 测试获取用户所听课程列表 - 用户没有所听课程的情况
     */
    @Test
    void testGetListeningCourse_NoCourses() {
        Integer uid = 1;
        when(courseMapper.findListenByUid(uid)).thenReturn(Collections.emptySet());

        List<Course> result = courseController.getListeningCourse(uid);

        assertEquals(0, result.size());
        verify(courseMapper, times(1)).findListenByUid(uid);
        verify(courseMapper, never()).findByCid(anyInt());
    }


    /**
     * 测试创建课程 - 成功场景
     */
    @Test
    void testCreateCourseSuccess() {
        // 创建一个 CreateCourseRequest 对象
        CreateCourseRequest courseRequest = new CreateCourseRequest();
        courseRequest.setTitle("Test Course");
        courseRequest.setDescription("This is a test course");
        courseRequest.setOrganizer_id(1);

//        // 创建一个 Course 对象
//        Course course = new Course();
//        course.setTitle("Test Course");
//        course.setDescription("This is a test course");
//        course.setOrganizer_id(1);

        // 模拟 CourseMapper 的 insertCourse 方法返回 1，表示插入成功
        when(courseMapper.insertCourse(any(Course.class))).thenAnswer(invocation -> {
            Course argCourse = invocation.getArgument(0);
            argCourse.setId(1); // 模拟 MyBatis 自动填充 id
            return 1;
        });

        // 调用 CourseController 的 createCourse 方法
        int result = courseController.createCourse(courseRequest);

        // 验证结果
        assertEquals(1, result);

        // 验证 CourseMapper 的 insertCourse 方法是否被调用
        verify(courseMapper, times(1)).insertCourse(any(Course.class));
    }


    /**
     * 测试创建课程 - 标题为空的失败场景
     */

    @Test
    void testCreateCourseWithEmptyTitle() {
        // 创建一个 CreateCourseRequest 对象，标题为空
        CreateCourseRequest courseRequest = new CreateCourseRequest();
        courseRequest.setTitle("");
        courseRequest.setDescription("This is a test course");
        courseRequest.setOrganizer_id(1);

        // 调用 CourseController 的 createCourse 方法
        int result = courseController.createCourse(courseRequest);

        // 验证结果：由于标题为空，应该返回 -1
        assertEquals(-1, result);

        // 验证 CourseMapper 的 insertCourse 方法未被调用
        verify(courseMapper, never()).insertCourse(any(Course.class));
    }
    /**
     * 测试创建课程 - 描述为空的成功场景
     */
    @Test
    void testCreateCourse_EmptyDescription() {
        // 创建一个 CreateCourseRequest 对象，描述为空
        CreateCourseRequest courseRequest = new CreateCourseRequest();
        courseRequest.setTitle("测试课程");
        courseRequest.setOrganizer_id(1);
        courseRequest.setDescription(null);

        // 创建一个 Course 对象，用于模拟插入后的结果
//        Course course = new Course();
//        course.setOrganizer_id(1);
//        course.setTitle("测试课程");
//        course.setDescription("");
//        course.setId(1);

        // 模拟 CourseMapper 的 insertCourse 方法返回 1，表示插入成功
        when(courseMapper.insertCourse(any(Course.class))).thenAnswer(invocation -> {
            Course argCourse = invocation.getArgument(0);
            argCourse.setId(1); // 模拟 MyBatis 自动填充 id
            return 1;
        });

        // 调用 CourseController 的 createCourse 方法
        int result = courseController.createCourse(courseRequest);

        // 验证结果
        assertEquals(1, result);

        // 验证 CourseMapper 的 insertCourse 方法是否被调用
        verify(courseMapper, times(1)).insertCourse(any(Course.class));
    }
    /**
     * 测试创建课程 - DAO 层插入失败的场景
     */
    @Test
    void testCreateCourse_DaoInsertFailure() {
        // 创建 CreateCourseRequest 对象
        CreateCourseRequest courseRequest = new CreateCourseRequest();
        courseRequest.setTitle("Test Course");
        courseRequest.setDescription("This is a test course");
        courseRequest.setOrganizer_id(1);

        // 创建 Course 对象
        Course course = new Course();
        course.setOrganizer_id(courseRequest.organizer_id());
        if (!StringUtils.hasText(courseRequest.title())) {
            // 这里不会执行，因为 title 不为空
        } else {
            course.setTitle(courseRequest.title());
        }
        if (courseRequest.description() == null) {
            course.setDescription("");
        } else {
            course.setDescription(courseRequest.description());
        }

        // 模拟 DAO 层插入失败，insertCourse 方法返回 0
        when(courseMapper.insertCourse(course)).thenReturn(0);

        // 执行测试：调用控制器方法创建课程
        int result = courseController.createCourse(courseRequest);

        // 验证结果：返回 -1 表示创建失败
        assertEquals(-1, result);
    }

    /**
     * 正常情况：传入有效的课程 ID，验证返回的课程信息是否与数据库中的记录一致
     */
    @Test
    void testGetCourseById_NormalCase() {
        // 定义有效的课程 ID
        int validCourseId = 1;

        // 创建模拟的课程对象
        Course mockCourse = new Course();
        mockCourse.setId(validCourseId);
        mockCourse.setTitle("有效课程标题");
        mockCourse.setDescription("有效课程描述");
        mockCourse.setOrganizer_id(1);

        // 模拟 CourseMapper 的 findByCid 方法返回模拟的课程对象
        when(courseMapper.findByCid(validCourseId)).thenReturn(mockCourse);

        // 调用控制器的 getCourseById 方法
        Course result = courseController.getCourseById(validCourseId);

        // 验证返回的课程信息是否与模拟的课程对象一致
        assertEquals(validCourseId, result.id());
        assertEquals("有效课程标题", result.title());
        assertEquals("有效课程描述", result.description());
        assertEquals(1, result.organizer_id());

        // 验证 CourseMapper 的 findByCid 方法是否被调用了一次，且传入的参数为有效的课程 ID
        verify(courseMapper, times(1)).findByCid(validCourseId);
    }

    /**
     * 边界情况：传入的课程 ID 不存在，验证返回的课程信息是否为空（或符合业务逻辑的默认值）
     */
    @Test
    void testGetCourseById_CourseNotFound() {
        // 定义不存在的课程 ID
        int nonExistentCourseId = 999;

        // 模拟 CourseMapper 的 findByCid 方法返回 null
        when(courseMapper.findByCid(nonExistentCourseId)).thenReturn(null);

        // 调用控制器的 getCourseById 方法
        Course result = courseController.getCourseById(nonExistentCourseId);

        // 验证返回的课程信息是否为 null
        assertNull(result);

        // 验证 CourseMapper 的 findByCid 方法是否被调用了一次，且传入的参数为不存在的课程 ID
        verify(courseMapper, times(1)).findByCid(nonExistentCourseId);
    }


    /**
     * 异常情况：传入的课程 ID 为 null 或者无效格式，验证系统的处理逻辑是否符合预期（如返回空对象或抛出异常）
     * 这里假设业务逻辑是传入无效 ID 时返回 null
     */
    @Test
    void testGetCourseById_InvalidId() {
        // 定义无效的课程 ID
        int invalidCourseId = -1;

        // 模拟 CourseMapper 的 findByCid 方法返回 null
        when(courseMapper.findByCid(invalidCourseId)).thenReturn(null);

        // 调用控制器的 getCourseById 方法
        Course result = courseController.getCourseById(invalidCourseId);

        // 验证返回的课程信息是否为 null
        assertNull(result);

        // 验证 CourseMapper 的 findByCid 方法是否被调用了一次，且传入的参数为无效的课程 ID
        verify(courseMapper, times(1)).findByCid(invalidCourseId);
    }

    /**
     * 测试创建课时 - 成功场景
     */
    @Test
    void testCreateSpeechSuccess() {
        // 创建一个 CreateSpeechRequest 对象
        CreateSpeechRequest speechRequest = new CreateSpeechRequest();
        speechRequest.setTitle("Test Speech");
        speechRequest.setSpeaker_id(1);
        speechRequest.setCourse_id(1);

        // 创建一个 Speech 对象
        Speech speech = new Speech();
        speech.setTitle("Test Speech");
        speech.setSpeaker_id(1);
        speech.setCourse_id(1);

        // 模拟 CourseMapper 的 insertSpeech 方法返回 1，表示插入成功
        when(courseMapper.insertSpeech(any(Speech.class))).thenAnswer(invocation -> {
            Speech argSpeech = invocation.getArgument(0);
            argSpeech.setId(1); // 模拟 MyBatis 自动填充 id
            return 1;
        });

        // 调用 CourseController 的 createSpeech 方法
        int result = courseController.createSpeech(speechRequest);

        // 验证结果
        assertEquals(1, result);

        // 验证 CourseMapper 的 insertSpeech 方法是否被调用
        verify(courseMapper, times(1)).insertSpeech(any(Speech.class));
    }

    /**
     * 测试创建课时 - 标题为空的失败场景
     */
    @Test
    void testCreateSpeechTitleEmpty() {
        // 创建一个标题为空的 CreateSpeechRequest 对象
        CreateSpeechRequest speechRequest = new CreateSpeechRequest();
        speechRequest.setTitle("");
        speechRequest.setSpeaker_id(1);
        speechRequest.setCourse_id(1);

        // 调用 CourseController 的 createSpeech 方法
        int result = courseController.createSpeech(speechRequest);

        // 验证结果为 -1，表示创建失败
        assertEquals(-1, result);

        // 验证 CourseMapper 的 insertSpeech 方法未被调用
        verify(courseMapper, never()).insertSpeech(any(Speech.class));
    }


    /**
     * 测试创建课时 - DAO 层插入失败的场景
     */
    @Test
    void testCreateSpeechDAOInsertFailure() {
        // 创建一个 CreateSpeechRequest 对象
        CreateSpeechRequest speechRequest = new CreateSpeechRequest();
        speechRequest.setTitle("Test Speech");
        speechRequest.setSpeaker_id(1);
        speechRequest.setCourse_id(1);

        // 模拟 CourseMapper 的 insertSpeech 方法返回 0，表示插入失败
        when(courseMapper.insertSpeech(any(Speech.class))).thenReturn(0);

        // 调用 CourseController 的 createSpeech 方法
        int result = courseController.createSpeech(speechRequest);

        // 验证结果为 -1，表示创建失败
        assertEquals(-1, result);

        // 验证 CourseMapper 的 insertSpeech 方法是否被调用
        verify(courseMapper, times(1)).insertSpeech(any(Speech.class));
    }

    /**
     * 正常情况：传入有效的课时 ID，验证返回的课时信息是否与数据库中的记录一致
     */
    @Test
    void testGetSpeechById_NormalCase() {
        // 定义有效的课时 ID
        int validSpeechId = 1;

        // 创建模拟的课时对象
        Speech mockSpeech = new Speech();
        mockSpeech.setId(validSpeechId);
        mockSpeech.setTitle("有效课时标题");
        mockSpeech.setSpeaker_id(1);
        mockSpeech.setCourse_id(1);

        // 模拟 CourseMapper 的 findSpeechById 方法返回模拟的课时对象
        when(courseMapper.findSpeechById(validSpeechId)).thenReturn(mockSpeech);

        // 调用控制器的 getSpeechById 方法
        Speech result = courseController.getSpeechById(validSpeechId);

        // 验证返回的课时信息是否与模拟的课时对象一致
        assertEquals(validSpeechId, result.id());
        assertEquals("有效课时标题", result.title());
        assertEquals(1, result.speaker_id());
        assertEquals(1, result.course_id());

        // 验证 CourseMapper 的 findSpeechById 方法是否被调用了一次，且传入的参数为有效的课时 ID
        verify(courseMapper, times(1)).findSpeechById(validSpeechId);
    }

    /**
     * 边界情况：传入的课时 ID 不存在，验证返回的课时信息是否为空（或符合业务逻辑的默认值）
     */
    @Test
    void testGetSpeechById_SpeechNotFound() {
        // 定义不存在的课时 ID
        int nonExistentSpeechId = 999;

        // 模拟 CourseMapper 的 findSpeechById 方法返回 null
        when(courseMapper.findSpeechById(nonExistentSpeechId)).thenReturn(null);

        // 调用控制器的 getSpeechById 方法
        Speech result = courseController.getSpeechById(nonExistentSpeechId);

        // 验证返回的课时信息是否为 null
        assertNull(result);

        // 验证 CourseMapper 的 findSpeechById 方法是否被调用了一次，且传入的参数为不存在的课时 ID
        verify(courseMapper, times(1)).findSpeechById(nonExistentSpeechId);
    }

    /**
     * 异常情况：传入的课时 ID 为无效格式，验证系统的处理逻辑是否符合预期（如返回空对象或抛出异常）
     * 这里假设业务逻辑是传入无效 ID 时返回 null
     */
    @Test
    void testGetSpeechById_InvalidId() {
        // 定义无效的课时 ID
        int invalidSpeechId = -1;

        // 模拟 CourseMapper 的 findSpeechById 方法返回 null
        when(courseMapper.findSpeechById(invalidSpeechId)).thenReturn(null);

        // 调用控制器的 getSpeechById 方法
        Speech result = courseController.getSpeechById(invalidSpeechId);

        // 验证返回的课时信息是否为 null
        assertNull(result);

        // 验证 CourseMapper 的 findSpeechById 方法是否被调用了一次，且传入的参数为无效的课时 ID
        verify(courseMapper, times(1)).findSpeechById(invalidSpeechId);
    }



    /**
     * 测试学生添加课程 - 正常情况
     */
    @Test
    void testAddCourse_NormalCase() {
        // 创建有效的 AddCourseRequest 对象
        AddCourseRequest addCourseRequest = new AddCourseRequest();
        addCourseRequest.setUid(1);
        addCourseRequest.setCourse_id(1);

        // 模拟 CourseMapper 的 insertCourse_Listener 方法返回 1，表示插入成功
        when(courseMapper.insertCourse_Listener(1, 1)).thenReturn(1);

        // 调用 CourseController 的 addCourse 方法
        int result = courseController.addCourse(addCourseRequest);

        // 验证结果为 1，表示添加成功
        assertEquals(1, result);

        // 验证 CourseMapper 的 insertCourse_Listener 方法是否被调用
        verify(courseMapper, times(1)).insertCourse_Listener(1, 1);
    }

    /**
     * 测试学生添加课程 - 边界情况：课程 ID 不存在
     */
    @Test
    void testAddCourse_CourseIdNotExist() {
        // 创建 AddCourseRequest 对象，课程 ID 不存在
        AddCourseRequest addCourseRequest = new AddCourseRequest();
        addCourseRequest.setUid(1);
        addCourseRequest.setCourse_id(999);

        // 模拟 CourseMapper 的 insertCourse_Listener 方法返回 0，表示插入失败
        when(courseMapper.insertCourse_Listener(1, 999)).thenReturn(0);

        // 调用 CourseController 的 addCourse 方法
        int result = courseController.addCourse(addCourseRequest);

        // 验证结果为 0，表示添加失败
        assertEquals(0, result);

        // 验证 CourseMapper 的 insertCourse_Listener 方法是否被调用
        verify(courseMapper, times(1)).insertCourse_Listener(1, 999);
    }

    /**
     * 测试学生添加课程 - 边界情况：用户 ID 不存在
     */
    @Test
    void testAddCourse_UserIdNotExist() {
        // 创建 AddCourseRequest 对象，用户 ID 不存在
        AddCourseRequest addCourseRequest = new AddCourseRequest();
        addCourseRequest.setUid(999);
        addCourseRequest.setCourse_id(1);

        // 模拟 CourseMapper 的 insertCourse_Listener 方法返回 0，表示插入失败
        when(courseMapper.insertCourse_Listener(999, 1)).thenReturn(0);

        // 调用 CourseController 的 addCourse 方法
        int result = courseController.addCourse(addCourseRequest);

        // 验证结果为 0，表示添加失败
        assertEquals(0, result);

        // 验证 CourseMapper 的 insertCourse_Listener 方法是否被调用
        verify(courseMapper, times(1)).insertCourse_Listener(999, 1);
    }

    /**
     * 测试学生添加课程 - 异常情况：课程 ID 为 null
     */
    @Test
    void testAddCourse_CourseIdNull() {
        // 创建 AddCourseRequest 对象，课程 ID 为 null
        AddCourseRequest addCourseRequest = new AddCourseRequest();
        addCourseRequest.setUid(1);
        // 这里假设 CourseController 会处理 null 情况，返回 0
        addCourseRequest.setCourse_id(0);

        // 模拟 CourseMapper 的 insertCourse_Listener 方法不被调用
        when(courseMapper.insertCourse_Listener(1, 0)).thenReturn(0);

        // 调用 CourseController 的 addCourse 方法
        int result = courseController.addCourse(addCourseRequest);

        // 验证结果为 0，表示添加失败
        assertEquals(0, result);

        // 验证 CourseMapper 的 insertCourse_Listener 方法是否被调用
        verify(courseMapper, times(1)).insertCourse_Listener(1, 0);
    }

    /**
     * 测试学生添加课程 - 异常情况：用户 ID 为 null
     */
    @Test
    void testAddCourse_UserIdNull() {
        // 创建 AddCourseRequest 对象，用户 ID 为 null
        AddCourseRequest addCourseRequest = new AddCourseRequest();
        addCourseRequest.setUid(0);
        addCourseRequest.setCourse_id(1);

        // 模拟 CourseMapper 的 insertCourse_Listener 方法不被调用
        when(courseMapper.insertCourse_Listener(0, 1)).thenReturn(0);

        // 调用 CourseController 的 addCourse 方法
        int result = courseController.addCourse(addCourseRequest);

        // 验证结果为 0，表示添加失败
        assertEquals(0, result);

        // 验证 CourseMapper 的 insertCourse_Listener 方法是否被调用
        verify(courseMapper, times(1)).insertCourse_Listener(0, 1);
    }
}