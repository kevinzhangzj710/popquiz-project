package cn.edu.njust.hearth.popquiz_backend;
import cn.edu.njust.hearth.popquiz_backend.controller.CommentController;
import cn.edu.njust.hearth.popquiz_backend.entity.Question_comments;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech_comments;
import cn.edu.njust.hearth.popquiz_backend.mapper.CommentMapper;
import cn.edu.njust.hearth.popquiz_backend.requestBody.CreateQueCommentRequest;
import cn.edu.njust.hearth.popquiz_backend.requestBody.CreateSpeCommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentControllerTest {

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 测试添加问题评论成功的情况
    @Test
    public void testAddQueCommentSuccess() {
        // 创建一个 CreateQueCommentRequest 对象，并设置问题ID、用户ID和评论内容
        CreateQueCommentRequest commentRequest = new CreateQueCommentRequest();
        commentRequest.setQuestion_id(1);
        commentRequest.setUser_id(1);
        commentRequest.setComment("Test comment");

        // 创建一个 Question_comments 对象，并设置相应的属性
        Question_comments questionComments = new Question_comments();
        questionComments.setQuestion_id(commentRequest.getQuestion_id());
        questionComments.setUser_id(commentRequest.getUser_id());
        questionComments.setComment(commentRequest.getComment());
        // 模拟插入成功后自动生成的ID
        int generatedId = 1;
        questionComments.setId(generatedId);

        // 模拟 CommentMapper 的 insertQueComment 方法返回 1，表示插入成功
        when(commentMapper.insertQueComment(any(Question_comments.class))).thenAnswer(invocation -> {
            Question_comments argComments = invocation.getArgument(0);
            argComments.setId(generatedId); // 模拟 MyBatis 自动填充 id
            return 1;
        });

        // 调用 CommentController 的 addQueComment 方法添加评论，并获取返回结果
        int result = commentController.addQueComment(commentRequest);

        // 验证返回的结果是否为插入后生成的ID
        assertEquals(generatedId, result);

        // 验证 CommentMapper 的 insertQueComment 方法是否被调用了一次
        verify(commentMapper, times(1)).insertQueComment(any(Question_comments.class));
    }

    // 测试添加问题评论失败的情况
    @Test
    public void testAddQueCommentFailure() {
        CreateQueCommentRequest commentRequest = new CreateQueCommentRequest();
        commentRequest.setQuestion_id(1);
        commentRequest.setUser_id(1);
        commentRequest.setComment("Test comment");

        when(commentMapper.insertQueComment(any(Question_comments.class))).thenReturn(0);

        int result = commentController.addQueComment(commentRequest);

        assertEquals(-1, result);
        verify(commentMapper, times(1)).insertQueComment(any(Question_comments.class));
    }

    // 测试删除问题评论成功的情况
    @Test
    public void testDeleteQueCommentSuccess() {
        int commentId = 1;
        when(commentMapper.deleteQueComment(commentId)).thenReturn(1);

        int result = commentController.deleteQueComment(commentId);

        assertEquals(1, result);
        verify(commentMapper, times(1)).deleteQueComment(commentId);
    }

    // 测试删除问题评论失败的情况
    @Test
    public void testDeleteQueCommentFailure() {
        int commentId = 1;
        when(commentMapper.deleteQueComment(commentId)).thenReturn(0);

        int result = commentController.deleteQueComment(commentId);

        assertEquals(0, result);
        verify(commentMapper, times(1)).deleteQueComment(commentId);
    }

    // 测试获取问题评论成功的情况
    @Test
    public void testGetQueCommentSuccess() {
        int commentId = 1;
        Question_comments questionComments = new Question_comments();
        questionComments.setId(commentId);
        questionComments.setQuestion_id(1);
        questionComments.setUser_id(1);
        questionComments.setComment("Test comment");

        when(commentMapper.selectQueComment(commentId)).thenReturn(questionComments);

        Question_comments result = commentController.getQueComment(commentId);

        assertEquals(commentId, result.getId());
        verify(commentMapper, times(1)).selectQueComment(commentId);
    }

    // 测试获取问题评论失败的情况
    @Test
    public void testGetQueCommentFailure() {
        int commentId = 1;
        when(commentMapper.selectQueComment(commentId)).thenReturn(null);

        Question_comments result = commentController.getQueComment(commentId);

        assertNull(result);
        verify(commentMapper, times(1)).selectQueComment(commentId);
    }

    // 测试获取问题所有评论成功的情况
    @Test
    public void testGetQueAllCommentsSuccess() {
        int questionId = 1;
        Set<Integer> que_comment_ids = new HashSet<>(Arrays.asList(1, 2));
        Question_comments comment1 = new Question_comments();
        comment1.setId(1);
        Question_comments comment2 = new Question_comments();
        comment2.setId(2);

        when(commentMapper.getQuestionComments_Ids(questionId)).thenReturn(que_comment_ids);
        when(commentMapper.selectQueComment(1)).thenReturn(comment1);
        when(commentMapper.selectQueComment(2)).thenReturn(comment2);

        List<Question_comments> result = commentController.getQueAllComments(questionId);

        assertEquals(2, result.size());
        verify(commentMapper, times(1)).getQuestionComments_Ids(questionId);
        verify(commentMapper, times(1)).selectQueComment(1);
        verify(commentMapper, times(1)).selectQueComment(2);
    }

    // 测试获取问题所有评论失败的情况
    @Test
    public void testGetQueAllCommentsFailure() {
        int questionId = 1;
        when(commentMapper.getQuestionComments_Ids(questionId)).thenReturn(Collections.emptySet());

        List<Question_comments> result = commentController.getQueAllComments(questionId);

        assertTrue(result.isEmpty());
        verify(commentMapper, times(1)).getQuestionComments_Ids(questionId);
        verify(commentMapper, never()).selectQueComment(anyInt());
    }

    // 测试添加演讲评论成功的情况
    @Test
    public void testAddSpeCommentSuccess() {
        // 创建一个 CreateSpeCommentRequest 对象，并设置演讲 ID、用户 ID 和评论内容
        CreateSpeCommentRequest commentRequest = new CreateSpeCommentRequest();
        commentRequest.setSpeech_id(1);
        commentRequest.setUser_id(1);
        commentRequest.setComment("Test comment");

        // 创建一个 Speech_comments 对象，并设置相应的属性
        Speech_comments speechComments = new Speech_comments();
        speechComments.setSpeech_id(commentRequest.getSpeech_id());
        speechComments.setUser_id(commentRequest.getUser_id());
        speechComments.setComment(commentRequest.getComment());

        // 模拟插入成功后自动生成的 ID
        int generatedId = 1;

        // 模拟 CommentMapper 的 insertSpeComment 方法返回 1，表示插入成功
        when(commentMapper.insertSpeComment(any(Speech_comments.class))).thenAnswer(invocation -> {
            Speech_comments argComments = invocation.getArgument(0);
            argComments.setId(generatedId); // 模拟 MyBatis 自动填充 id
            return 1;
        });

        // 调用 CommentController 的 addSpeComment 方法添加评论，并获取返回结果
        int result = commentController.addSpeComment(commentRequest);

        // 验证返回的结果是否为插入后生成的 ID
        assertEquals(generatedId, result);

        // 验证 CommentMapper 的 insertSpeComment 方法是否被调用了一次
        verify(commentMapper, times(1)).insertSpeComment(any(Speech_comments.class));
    }
    // 测试添加演讲评论失败的情况
    @Test
    public void testAddSpeCommentFailure() {
        CreateSpeCommentRequest commentRequest = new CreateSpeCommentRequest();
        commentRequest.setSpeech_id(1);
        commentRequest.setUser_id(1);
        commentRequest.setComment("Test comment");

        when(commentMapper.insertSpeComment(any(Speech_comments.class))).thenReturn(0);

        int result = commentController.addSpeComment(commentRequest);

        assertEquals(-1, result);
        verify(commentMapper, times(1)).insertSpeComment(any(Speech_comments.class));
    }

    // 测试删除演讲评论成功的情况
    @Test
    public void testDeleteSpeCommentSuccess() {
        int commentId = 1;
        when(commentMapper.deleteSpeComment(commentId)).thenReturn(1);

        int result = commentController.deleteSpeComment(commentId);

        assertEquals(1, result);
        verify(commentMapper, times(1)).deleteSpeComment(commentId);
    }

    // 测试删除演讲评论失败的情况
    @Test
    public void testDeleteSpeCommentFailure() {
        int commentId = 1;
        when(commentMapper.deleteSpeComment(commentId)).thenReturn(0);

        int result = commentController.deleteSpeComment(commentId);

        assertEquals(0, result);
        verify(commentMapper, times(1)).deleteSpeComment(commentId);
    }

    // 测试获取演讲评论成功的情况
    @Test
    public void testGetSpeCommentSuccess() {
        int commentId = 1;
        Speech_comments speechComments = new Speech_comments();
        speechComments.setId(commentId);
        speechComments.setSpeech_id(1);
        speechComments.setUser_id(1);
        speechComments.setComment("Test comment");

        when(commentMapper.selectSpeComment(commentId)).thenReturn(speechComments);

        Speech_comments result = commentController.getSpeComment(commentId);

        assertEquals(commentId, result.getId());
        verify(commentMapper, times(1)).selectSpeComment(commentId);
    }

    // 测试获取演讲评论失败的情况
    @Test
    public void testGetSpeCommentFailure() {
        int commentId = 1;
        when(commentMapper.selectSpeComment(commentId)).thenReturn(null);

        Speech_comments result = commentController.getSpeComment(commentId);

        assertNull(result);
        verify(commentMapper, times(1)).selectSpeComment(commentId);
    }

    // 测试获取演讲所有评论成功的情况
    @Test
    public void testGetSpeAllCommentsSuccess() {
        int speechId = 1;
        Set<Integer> spe_comments_ids = new HashSet<>(Arrays.asList(1, 2));
        Speech_comments comment1 = new Speech_comments();
        comment1.setId(1);
        Speech_comments comment2 = new Speech_comments();
        comment2.setId(2);

        when(commentMapper.getSpeechComments_Ids(speechId)).thenReturn(spe_comments_ids);
        when(commentMapper.selectSpeComment(1)).thenReturn(comment1);
        when(commentMapper.selectSpeComment(2)).thenReturn(comment2);

        List<Speech_comments> result = commentController.getSpeAllComments(speechId);

        assertEquals(2, result.size());
        verify(commentMapper, times(1)).getSpeechComments_Ids(speechId);
        verify(commentMapper, times(1)).selectSpeComment(1);
        verify(commentMapper, times(1)).selectSpeComment(2);
    }

    // 测试获取演讲所有评论失败的情况
    @Test
    public void testGetSpeAllCommentsFailure() {
        int speechId = 1;
        when(commentMapper.getSpeechComments_Ids(speechId)).thenReturn(Collections.emptySet());

        List<Speech_comments> result = commentController.getSpeAllComments(speechId);

        assertTrue(result.isEmpty());
        verify(commentMapper, times(1)).getSpeechComments_Ids(speechId);
        verify(commentMapper, never()).selectSpeComment(anyInt());
    }
}