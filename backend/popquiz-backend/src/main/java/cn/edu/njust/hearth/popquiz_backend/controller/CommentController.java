package cn.edu.njust.hearth.popquiz_backend.controller;
import cn.edu.njust.hearth.popquiz_backend.entity.Question;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech_comments;
import cn.edu.njust.hearth.popquiz_backend.entity.Question_comments;
import cn.edu.njust.hearth.popquiz_backend.mapper.CommentMapper;
import cn.edu.njust.hearth.popquiz_backend.mapper.QuestionMapper;
import cn.edu.njust.hearth.popquiz_backend.requestBody.*;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    private CommentMapper  commentMapper;
    @PostMapping("/addQueComment")
    @Operation(summary = "用户添加对于某个quiz的评论",description = "添加成功返回该评论的id，失败返回-1")
    public int addQueComment(@RequestBody CreateQueCommentRequest commentRequest){
        Question_comments questionComments = new Question_comments();
        questionComments.setQuestion_id(commentRequest.getQuestion_id());
        questionComments.setUser_id(commentRequest.getUser_id());
        questionComments.setComment(commentRequest.getComment());
        if(commentMapper.insertQueComment(questionComments) == 1){
            return questionComments.id;
        }
        else return -1;
    }

    @PostMapping("deleteQueComment")
    @Operation(summary = "学生删除对某个quiz的评论",description = "成功返回一个>0的数表示删掉的记录数，失败返回0")
    public int deleteQueComment(@RequestParam int comment_id){
        return  commentMapper.deleteQueComment(comment_id);
    }

    @GetMapping("getQueComment")
    @Operation(summary = "根据某个问题评论的id获取该评论",description = "添加成功返回该评论的实体，失败返回空实体")
    public Question_comments getQueComment(@RequestParam int comment_id){
        Question_comments questionComments = commentMapper.selectQueComment(comment_id);
        return questionComments;
    }

    @GetMapping("getQueAllComments")
    @Operation(summary = "根据quiz的id获取该quiz的全部用户的评论",description = "获取成功返回一个评论列表，失败返回空列表")
    public List<Question_comments> getQueAllComments(@RequestParam int question_id){
        List<Question_comments>questionCommentsList = new ArrayList<>();
        Set<Integer>que_comment_ids = commentMapper.getQuestionComments_Ids(question_id);
        for(Integer id : que_comment_ids){
            Question_comments questionComments = commentMapper.selectQueComment(id);
            questionCommentsList.add(questionComments);
        }
        return questionCommentsList;
    }

    @PostMapping("/addSpeComment")
    @Operation(summary = "用户添加对于某个speech的评论",description = "添加成功返回该评论的id，失败返回-1")
    public int addSpeComment(@RequestBody CreateSpeCommentRequest commentRequest){
        Speech_comments speechComments = new Speech_comments();
        speechComments.setComment(commentRequest.getComment());
        speechComments.setUser_id(commentRequest.getUser_id());
        speechComments.setSpeech_id(commentRequest.getSpeech_id());
        if(commentMapper.insertSpeComment(speechComments) == 1){
            return speechComments.id;
        }
        else return -1;
    }

    @PostMapping("deleteSpeComment")
    @Operation(summary = "学生删除对某个speech的评论",description = "成功返回一个>0的数表示删掉的记录数，失败返回0")
    public int deleteSpeComment(@RequestParam int comment_id){
        return  commentMapper.deleteSpeComment(comment_id);
    }

    @GetMapping("getSpeComment")
    @Operation(summary = "根据某个speech评论的id获取该评论",description = "添加成功返回该评论的实体，失败返回空实体")
    public Speech_comments getSpeComment(@RequestParam int comment_id){
        Speech_comments speechComments = commentMapper.selectSpeComment(comment_id);
        return  speechComments;
    }

    @GetMapping("getSpeAllComments")
    @Operation(summary = "根据speech的id获取该场speech的全部用户的评论",description = "获取成功返回一个评论列表，失败返回空列表")
    public List<Speech_comments> getSpeAllComments(@RequestParam int speech_id){
        List<Speech_comments>speechCommentsList = new ArrayList<>();
        Set<Integer>spe_comments_ids = commentMapper.getSpeechComments_Ids(speech_id);
        for(Integer id : spe_comments_ids){
            Speech_comments  speechComments = commentMapper.selectSpeComment(id);
            speechCommentsList.add(speechComments);
        }
        return speechCommentsList;
    }
}
