package cn.edu.njust.hearth.popquiz_backend.mapper;
import cn.edu.njust.hearth.popquiz_backend.entity.Course;
import cn.edu.njust.hearth.popquiz_backend.entity.Question;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech;
import cn.edu.njust.hearth.popquiz_backend.entity.Submit;
import org.apache.ibatis.annotations.*;

import java.util.Set;

@Mapper
public interface QuestionMapper {
    @Insert("INSERT INTO QUESTIONS (QUESTION,SELECTION,ANSWER,SPEECH_ID,START_TIME,END_TIME) VALUES (#{question},#{selection},#{answer},#{speech_id},#{start_time},#{end_time})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertQuestion(Question que);

    @Select("SELECT * FROM QUESTIONS WHERE ID = #{qid}")
    public Question findQuestionById(@Param("qid") Integer qid);

    @Insert("INSERT INTO SUBMITS (QUESTION_ID,USER_ID,ANSWER) VALUES (#{question_id},#{user_id},#{answer})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSubmit(Submit submit);

    @Select("SELECT * FROM SUBMITS WHERE ID = #{subid}")
    public Submit findSubmitById(@Param("subid") Integer subid);

    @Select("SELECT ID FROM QUESTIONS WHERE SPEECH_ID = #{sp_id}")
    public Set<Integer> findQuestionIdsBySpeechId(@Param("sp_id") Integer sp_id);

    @Select("SELECT ID FROM SUBMITS WHERE QUESTION_ID = #{qid} AND USER_ID = #{uid}")
    public Integer getSubmitId(@Param("qid") int qid, @Param("uid") int uid);

    @Select("SELECT ANSWER FROM SUBMITS WHERE QUESTION_ID = #{qid} AND USER_ID = #{uid}")
    public char judgingAnswer(@Param("qid") int qid, @Param("uid") int uid);

}
