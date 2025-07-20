package cn.edu.njust.hearth.popquiz_backend.mapper;
import cn.edu.njust.hearth.popquiz_backend.entity.Question_comments;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech_comments;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech;
import cn.edu.njust.hearth.popquiz_backend.entity.Submit;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.*;

import java.util.Set;
@Mapper
public interface CommentMapper {
    @Insert("INSERT INTO QUESTION_COMMENTS (COMMENT,QUESTION_ID,USER_ID) VALUES (#{comment},#{question_id},#{user_id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insertQueComment(Question_comments questionComments);

    @Delete("DELETE FROM QUESTION_COMMENTS WHERE ID = #{id}")
    public int deleteQueComment(@Param("id")  int id);

    @Select("SELECT * FROM QUESTION_COMMENTS WHERE ID = #{id}")
    public Question_comments selectQueComment(@Param("id")  int id);

    @Select("SELECT ID FROM QUESTION_COMMENTS WHERE QUESTION_ID = #{qid}")
    public Set<Integer>getQuestionComments_Ids(@Param("qid") int qid);

    @Insert("INSERT INTO SPEECH_COMMENTS (COMMENT,SPEECH_ID,USER_ID) VALUES (#{comment},#{speech_id},#{user_id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insertSpeComment(Speech_comments speechComments);

    @Delete("DELETE FROM SPEECH_COMMENTS WHERE ID = #{id}")
    public int deleteSpeComment(@Param("id")  int id);

    @Select("SELECT * FROM SPEECH_COMMENTS WHERE ID = #{id}")
    public Speech_comments selectSpeComment(@Param("id")  int id);

    @Select("SELECT ID FROM SPEECH_COMMENTS WHERE SPEECH_ID = #{spid}")
    public Set<Integer>getSpeechComments_Ids(@Param("spid") int spid);
}
