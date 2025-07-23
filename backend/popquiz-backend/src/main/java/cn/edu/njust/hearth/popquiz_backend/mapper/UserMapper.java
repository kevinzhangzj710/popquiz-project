package cn.edu.njust.hearth.popquiz_backend.mapper;

import cn.edu.njust.hearth.popquiz_backend.entity.Course;
import cn.edu.njust.hearth.popquiz_backend.entity.Course_listener;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech;
import cn.edu.njust.hearth.popquiz_backend.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USERS WHERE USERNAME = #{username} AND PASSWORD = #{password}")
    public List<User> findByUsernameAndPassword(
            @Param("username") String username,
            @Param("password") String password);

    @Insert("INSERT INTO USERS (USERNAME, PASSWORD, NAME) VALUES (#{user.username},#{user.password},#{user.name})")
    // @Options(useGeneratedKeys = true, keyProperty = "id")
    void register(@Param("user") User user);

    @Select("select * from speeches where speaker_id=#{user_id} and id=#{speech_id}")
    public Speech findSpeechById(@Param("user_id") int user_id, @Param("speech_id") int speech_id);

    @Select("select * from courses where organizer_id=#{user_id} and id= (select course_id from speeches where id=#{speech_id})")
    public Course findCourseById(@Param("user_id") int user_id, @Param("speech_id") int speech_id);

    @Select("SELECT * FROM COURSE_LISTENER WHERE USER_ID = #{user_id} and COURSE_ID = (select course_id from speeches where id=#{speech_id}) limit 1")
    public Course_listener findCour_Listener(@Param("user_id") int user_id, @Param("speech_id") int speech_id);

}
