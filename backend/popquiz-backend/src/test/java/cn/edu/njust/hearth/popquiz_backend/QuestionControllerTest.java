package cn.edu.njust.hearth.popquiz_backend;

import cn.edu.njust.hearth.popquiz_backend.controller.QuestionController;
import cn.edu.njust.hearth.popquiz_backend.entity.Question;
import cn.edu.njust.hearth.popquiz_backend.entity.ResultOfQuestion;
import cn.edu.njust.hearth.popquiz_backend.entity.Submit;
import cn.edu.njust.hearth.popquiz_backend.mapper.QuestionMapper;
import cn.edu.njust.hearth.popquiz_backend.requestBody.CreateQuestionRequest;
import cn.edu.njust.hearth.popquiz_backend.requestBody.CreateSubmitRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class QuestionControllerTest {

    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private QuestionController questionController;

    @BeforeEach
    void setUp() {
        // 初始化 Mockito 注解，使得 @Mock 和 @InjectMocks 生效
        MockitoAnnotations.openMocks(this);
    }

    // 测试创建问题成功的情况
    @Test
    void testCreateQuestionSuccess() {
        // 创建一个 CreateQuestionRequest 对象，并设置问题相关信息
        CreateQuestionRequest createQuestionRequest = new CreateQuestionRequest();
        createQuestionRequest.setQuestion("Test Question");
        createQuestionRequest.setAnswer('A');
        createQuestionRequest.setSelection("A. Option A; B. Option B");
        createQuestionRequest.setStart_time(new Timestamp(System.currentTimeMillis()));
        createQuestionRequest.setEnd_time(new Timestamp(System.currentTimeMillis() + 3600000));
        createQuestionRequest.setSpeech_id(1);

        Question question = new Question();
        question.setQuestion(createQuestionRequest.getQuestion());
        question.setAnswer(createQuestionRequest.getAnswer());
        question.setSelection(createQuestionRequest.getSelection());
        question.setStart_time(createQuestionRequest.getStart_time());
        question.setEnd_time(createQuestionRequest.getEnd_time());
        question.setSpeech_id(createQuestionRequest.getSpeech_id());

        // 模拟 questionMapper 的 insertQuestion 方法返回 1，表示插入成功
        when(questionMapper.insertQuestion(any(Question.class))).thenAnswer(invocation -> {
            Question argQuestion = invocation.getArgument(0);
            argQuestion.setId(1); // 模拟 MyBatis 自动填充 id
            return 1;
        });

        // 调用 QuestionController 的 CreateQuestion 方法进行创建问题操作
        int result = questionController.CreateQuestion(createQuestionRequest);

        // 验证返回结果是否为问题的 id
        assertEquals(1, result);

        // 验证 questionMapper 的 insertQuestion 方法是否被调用了一次
        verify(questionMapper, times(1)).insertQuestion(any(Question.class));
    }

    // 测试创建问题失败的情况
    @Test
    void testCreateQuestionFailure() {
        // 创建一个 CreateQuestionRequest 对象，并设置问题相关信息
        CreateQuestionRequest createQuestionRequest = new CreateQuestionRequest();
        createQuestionRequest.setQuestion("Test Question");
        createQuestionRequest.setAnswer('A');
        createQuestionRequest.setSelection("A. Option A; B. Option B");
        createQuestionRequest.setStart_time(new Timestamp(System.currentTimeMillis()));
        createQuestionRequest.setEnd_time(new Timestamp(System.currentTimeMillis() + 3600000));
        createQuestionRequest.setSpeech_id(1);

        // 模拟 questionMapper 的 insertQuestion 方法返回 0，表示插入失败
        when(questionMapper.insertQuestion(any(Question.class))).thenReturn(0);

        // 调用 QuestionController 的 CreateQuestion 方法进行创建问题操作
        int result = questionController.CreateQuestion(createQuestionRequest);

        // 验证返回结果是否为 -1
        assertEquals(-1, result);

        // 验证 questionMapper 的 insertQuestion 方法是否被调用了一次
        verify(questionMapper, times(1)).insertQuestion(any(Question.class));
    }

    // 测试通过问题的 id 获取该问题成功的情况
    @Test
    void testGetQuestionByIdSuccess() {
        // 定义有效的问题 ID
        int validQuestionId = 1;

        // 创建模拟的问题对象
        Question mockQuestion = new Question();
        mockQuestion.setId(validQuestionId);
        mockQuestion.setQuestion("Test Question");
        mockQuestion.setAnswer('A');
        mockQuestion.setSelection("A. Option A; B. Option B");
        mockQuestion.setStart_time(new Timestamp(System.currentTimeMillis()));
        mockQuestion.setEnd_time(new Timestamp(System.currentTimeMillis() + 3600000));
        mockQuestion.setSpeech_id(1);

        // 模拟 questionMapper 的 findQuestionById 方法返回模拟的问题对象
        when(questionMapper.findQuestionById(validQuestionId)).thenReturn(mockQuestion);

        // 调用 QuestionController 的 getQuestionById 方法
        Question result = questionController.getQuestionById(validQuestionId);

        // 验证返回的问题信息是否与模拟的问题对象一致
        assertEquals(validQuestionId, result.getId());
        assertEquals("Test Question", result.getQuestion());
        assertEquals('A', result.getAnswer());
        assertEquals("A. Option A; B. Option B", result.getSelection());

        // 验证 questionMapper 的 findQuestionById 方法是否被调用了一次，且传入的参数为有效的问题 ID
        verify(questionMapper, times(1)).findQuestionById(validQuestionId);
    }

    // 测试通过问题的 id 获取该问题失败的情况
    @Test
    void testGetQuestionByIdFailure() {
        // 定义无效的问题 ID
        int invalidQuestionId = -1;

        // 模拟 questionMapper 的 findQuestionById 方法返回 null
        when(questionMapper.findQuestionById(invalidQuestionId)).thenReturn(null);

        // 调用 QuestionController 的 getQuestionById 方法
        Question result = questionController.getQuestionById(invalidQuestionId);

        // 验证返回的问题信息是否为 null
        assertEquals(null, result);

        // 验证 questionMapper 的 findQuestionById 方法是否被调用了一次，且传入的参数为无效的问题 ID
        verify(questionMapper, times(1)).findQuestionById(invalidQuestionId);
    }

    // 测试通过 speech_id 获取该课时的所有 quiz 成功的情况
    @Test
    void testGetQuestionListSuccess() {
        // 定义有效的 speech ID
        int validSpeechId = 1;

        // 创建模拟的问题 ID 集合
        Set<Integer> questionIds = new HashSet<>();
        questionIds.add(1);
        questionIds.add(2);

        // 创建模拟的问题对象
        Question question1 = new Question();
        question1.setId(1);
        question1.setQuestion("Test Question 1");
        question1.setAnswer('A');
        question1.setSelection("A. Option A; B. Option B");
        question1.setStart_time(new Timestamp(System.currentTimeMillis()));
        question1.setEnd_time(new Timestamp(System.currentTimeMillis() + 3600000));
        question1.setSpeech_id(validSpeechId);

        Question question2 = new Question();
        question2.setId(2);
        question2.setQuestion("Test Question 2");
        question2.setAnswer('B');
        question2.setSelection("A. Option A; B. Option B");
        question2.setStart_time(new Timestamp(System.currentTimeMillis()));
        question2.setEnd_time(new Timestamp(System.currentTimeMillis() + 3600000));
        question2.setSpeech_id(validSpeechId);

        // 模拟 questionMapper 的 findQuestionIdsBySpeechId 方法返回模拟的问题 ID 集合
        when(questionMapper.findQuestionIdsBySpeechId(validSpeechId)).thenReturn(questionIds);
        // 模拟 questionMapper 的 findQuestionById 方法返回模拟的问题对象
        when(questionMapper.findQuestionById(1)).thenReturn(question1);
        when(questionMapper.findQuestionById(2)).thenReturn(question2);

        // 调用 QuestionController 的 getQuestionList 方法
        List<Question> result = questionController.getQuestionList(validSpeechId);

        // 验证返回的问题列表的大小是否为 2
        assertEquals(2, result.size());
        // 验证返回的第一个问题的 id 是否为 1
        assertEquals(1, result.get(0).getId());
        // 验证返回的第二个问题的 id 是否为 2
        assertEquals(2, result.get(1).getId());

        // 验证 questionMapper 的 findQuestionIdsBySpeechId 方法是否被调用了一次，且传入的参数为有效的 speech ID
        verify(questionMapper, times(1)).findQuestionIdsBySpeechId(validSpeechId);
        // 验证 questionMapper 的 findQuestionById 方法是否被调用了两次
        verify(questionMapper, times(1)).findQuestionById(1);
        verify(questionMapper, times(1)).findQuestionById(2);
    }

    // 测试通过 speech_id 获取该课时的所有 quiz 失败的情况
    @Test
    void testGetQuestionListFailure() {
        // 定义有效的 speech ID
        int validSpeechId = 1;

        // 模拟 questionMapper 的 findQuestionIdsBySpeechId 方法返回空集合
        when(questionMapper.findQuestionIdsBySpeechId(validSpeechId)).thenReturn(Collections.emptySet());

        // 调用 QuestionController 的 getQuestionList 方法
        List<Question> result = questionController.getQuestionList(validSpeechId);

        // 验证返回的问题列表的大小是否为 0
        assertEquals(0, result.size());

        // 验证 questionMapper 的 findQuestionIdsBySpeechId 方法是否被调用了一次，且传入的参数为有效的 speech ID
        verify(questionMapper, times(1)).findQuestionIdsBySpeechId(validSpeechId);
    }

    // 测试学生提交某个 quiz 的答案成功的情况
    @Test
    void testCreateSubmitSuccess() {
        // 创建一个 CreateSubmitRequest 对象，并设置提交相关信息
        CreateSubmitRequest createSubmitRequest = new CreateSubmitRequest();
        createSubmitRequest.setQuestion_id(1);
        createSubmitRequest.setAnswer('A');
        createSubmitRequest.setUser_id(1);

        Submit submit = new Submit();
        submit.setQuestion_id(createSubmitRequest.getQuestion_id());
        submit.setAnswer(createSubmitRequest.getAnswer());
        submit.setUser_id(createSubmitRequest.getUser_id());

        // 模拟 questionMapper 的 insertSubmit 方法返回 1，表示插入成功
        when(questionMapper.insertSubmit(any(Submit.class))).thenAnswer(invocation -> {
            Submit argSubmit = invocation.getArgument(0);
            argSubmit.setId(1); // 模拟 MyBatis 自动填充 id
            return 1;
        });

        // 调用 QuestionController 的 CreateSubmit 方法进行提交答案操作
        int result = questionController.CreateSubmit(createSubmitRequest);

        // 验证返回结果是否为提交的 id
        assertEquals(1, result);

        // 验证 questionMapper 的 insertSubmit 方法是否被调用了一次
        verify(questionMapper, times(1)).insertSubmit(any(Submit.class));
    }

    // 测试学生提交某个 quiz 的答案失败的情况
    @Test
    void testCreateSubmitFailure() {
        // 创建一个 CreateSubmitRequest 对象，并设置提交相关信息
        CreateSubmitRequest createSubmitRequest = new CreateSubmitRequest();
        createSubmitRequest.setQuestion_id(1);
        createSubmitRequest.setAnswer('A');
        createSubmitRequest.setUser_id(1);

        // 模拟 questionMapper 的 insertSubmit 方法返回 0，表示插入失败
        when(questionMapper.insertSubmit(any(Submit.class))).thenReturn(0);

        // 调用 QuestionController 的 CreateSubmit 方法进行提交答案操作
        int result = questionController.CreateSubmit(createSubmitRequest);

        // 验证返回结果是否为 -1
        assertEquals(-1, result);

        // 验证 questionMapper 的 insertSubmit 方法是否被调用了一次
        verify(questionMapper, times(1)).insertSubmit(any(Submit.class));
    }

    // 测试通过 quiz 的 id 和学生的 id，获取到学生对于该 quiz 的 submit_id 成功的情况
    @Test
    void testGetSubmitIdSuccess() {
        // 定义有效的 quiz ID 和学生 ID
        int validQuizId = 1;
        int validUserId = 1;

        // 模拟 questionMapper 的 getSubmitId 方法返回有效的 submit ID
        when(questionMapper.getSubmitId(validQuizId, validUserId)).thenReturn(1);

        // 调用 QuestionController 的 GetSubmitId 方法
        Integer result = questionController.GetSubmitId(validQuizId, validUserId);

        // 验证返回的 submit ID 是否为 1
        assertEquals(1, result);

        // 验证 questionMapper 的 getSubmitId 方法是否被调用了一次，且传入的参数为有效的 quiz ID 和学生 ID
        verify(questionMapper, times(1)).getSubmitId(validQuizId, validUserId);
    }

    // 测试通过 quiz 的 id 和学生的 id，获取到学生对于该 quiz 的 submit_id 失败的情况
    @Test
    void testGetSubmitIdFailure() {
        // 定义有效的 quiz ID 和学生 ID
        int validQuizId = 1;
        int validUserId = 1;

        // 模拟 questionMapper 的 getSubmitId 方法返回 null
        when(questionMapper.getSubmitId(validQuizId, validUserId)).thenReturn(null);

        // 调用 QuestionController 的 GetSubmitId 方法
        Integer result = questionController.GetSubmitId(validQuizId, validUserId);

        // 验证返回的 submit ID 是否为 -1
        assertEquals(-1, result);

        // 验证 questionMapper 的 getSubmitId 方法是否被调用了一次，且传入的参数为有效的 quiz ID 和学生 ID
        verify(questionMapper, times(1)).getSubmitId(validQuizId, validUserId);
    }

    // 测试通过 submit 的 id 获取该 submit 成功的情况
    @Test
    void testGetSubmitByIdSuccess() {
        // 定义有效的 submit ID
        int validSubmitId = 1;

        // 创建模拟的提交对象
        Submit mockSubmit = new Submit();
        mockSubmit.setId(validSubmitId);
        mockSubmit.setQuestion_id(1);
        mockSubmit.setAnswer('A');
        mockSubmit.setUser_id(1);

        // 模拟 questionMapper 的 findSubmitById 方法返回模拟的提交对象
        when(questionMapper.findSubmitById(validSubmitId)).thenReturn(mockSubmit);

        // 调用 QuestionController 的 getSubmitById 方法
        Submit result = questionController.getSubmitById(validSubmitId);

        // 验证返回的提交信息是否与模拟的提交对象一致
        assertEquals(validSubmitId, result.getId());
        assertEquals(1, result.getQuestion_id());
        assertEquals('A', result.getAnswer());
        assertEquals(1, result.getUser_id());

        // 验证 questionMapper 的 findSubmitById 方法是否被调用了一次，且传入的参数为有效的 submit ID
        verify(questionMapper, times(1)).findSubmitById(validSubmitId);
    }

    // 测试通过 submit 的 id 获取该 submit 失败的情况
    @Test
    void testGetSubmitByIdFailure() {
        // 定义无效的 submit ID
        int invalidSubmitId = -1;

        // 模拟 questionMapper 的 findSubmitById 方法返回 null
        when(questionMapper.findSubmitById(invalidSubmitId)).thenReturn(null);

        // 调用 QuestionController 的 getSubmitById 方法
        Submit result = questionController.getSubmitById(invalidSubmitId);

        // 验证返回的提交信息是否为 null
        assertEquals(null, result);

        // 验证 questionMapper 的 findSubmitById 方法是否被调用了一次，且传入的参数为无效的 submit ID
        verify(questionMapper, times(1)).findSubmitById(invalidSubmitId);
    }

    // 测试判断学生对于某道题目的答案对错成功的情况
    @Test
    void testJudgeSubmitSuccess() {
        // 定义有效的问题 ID 和学生 ID
        int validQuestionId = 1;
        int validUserId = 1;

        // 创建模拟的问题对象
        Question mockQuestion = new Question();
        mockQuestion.setId(validQuestionId);
        mockQuestion.setQuestion("Test Question");
        mockQuestion.setAnswer('A');
        mockQuestion.setSelection("A. Option A; B. Option B");
        mockQuestion.setStart_time(new Timestamp(System.currentTimeMillis()));
        mockQuestion.setEnd_time(new Timestamp(System.currentTimeMillis() + 3600000));
        mockQuestion.setSpeech_id(1);

        // 模拟 questionMapper 的 judgingAnswer 方法返回正确的答案
        when(questionMapper.judgingAnswer(validQuestionId, validUserId)).thenReturn('A');
        // 模拟 questionMapper 的 findQuestionById 方法返回模拟的问题对象
        when(questionMapper.findQuestionById(validQuestionId)).thenReturn(mockQuestion);

        // 调用 QuestionController 的 judgeSubmit 方法
        boolean result = questionController.judgeSubmit(validQuestionId, validUserId);

        // 验证返回的结果是否为 true
        assertTrue(result);

        // 验证 questionMapper 的 judgingAnswer 方法是否被调用了一次，且传入的参数为有效的问题 ID 和学生 ID
        verify(questionMapper, times(1)).judgingAnswer(validQuestionId, validUserId);
        // 验证 questionMapper 的 findQuestionById 方法是否被调用了一次，且传入的参数为有效的问题 ID
        verify(questionMapper, times(1)).findQuestionById(validQuestionId);
    }

    // 测试获取在本次 speech 中，我答对了多少题成功的情况
    @Test
    void testGetNumofRightSuccess() {
        // 定义有效的 speech ID 和学生 ID
        int validSpeechId = 1;
        int validUserId = 1;

        // 创建模拟的问题 ID 集合
        Set<Integer> questionIds = new HashSet<>();
        questionIds.add(1);
        questionIds.add(2);

        // 创建模拟的问题对象
        Question question1 = new Question();
        question1.setId(1);
        question1.setQuestion("Test Question 1");
        question1.setAnswer('A');
        question1.setSelection("A. Option A; B. Option B");
        question1.setStart_time(new Timestamp(System.currentTimeMillis()));
        question1.setEnd_time(new Timestamp(System.currentTimeMillis() + 3600000));
        question1.setSpeech_id(validSpeechId);

        Question question2 = new Question();
        question2.setId(2);
        question2.setQuestion("Test Question 2");
        question2.setAnswer('B');
        question2.setSelection("A. Option A; B. Option B");
        question2.setStart_time(new Timestamp(System.currentTimeMillis()));
        question2.setEnd_time(new Timestamp(System.currentTimeMillis() + 3600000));
        question2.setSpeech_id(validSpeechId);

        // 模拟 questionMapper 的 findQuestionIdsBySpeechId 方法返回模拟的问题 ID 集合
        when(questionMapper.findQuestionIdsBySpeechId(validSpeechId)).thenReturn(questionIds);
        // 模拟 questionMapper 的 findQuestionById 方法返回模拟的问题对象
        when(questionMapper.findQuestionById(1)).thenReturn(question1);
        when(questionMapper.findQuestionById(2)).thenReturn(question2);
        // 模拟 questionMapper 的 judgingAnswer 方法返回正确的答案
        when(questionMapper.judgingAnswer(1, validUserId)).thenReturn('A');
        when(questionMapper.judgingAnswer(2, validUserId)).thenReturn('B');

        // 调用 QuestionController 的 getNumofRight 方法
        int result = questionController.getNumofRight(validSpeechId, validUserId);

        // 验证返回的答对题目的数量是否为 2
        assertEquals(2, result);

        // 验证 questionMapper 的 findQuestionIdsBySpeechId 方法是否被调用了一次，且传入的参数为有效的 speech ID
        verify(questionMapper, times(1)).findQuestionIdsBySpeechId(validSpeechId);
        // 验证 questionMapper 的 findQuestionById 方法是否被调用了两次
        verify(questionMapper, times(1)).findQuestionById(1);
        verify(questionMapper, times(1)).findQuestionById(2);
        // 验证 questionMapper 的 judgingAnswer 方法是否被调用了两次
        verify(questionMapper, times(1)).judgingAnswer(1, validUserId);
        verify(questionMapper, times(1)).judgingAnswer(2, validUserId);
    }




}