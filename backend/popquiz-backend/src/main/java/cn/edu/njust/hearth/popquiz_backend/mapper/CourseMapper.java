package cn.edu.njust.hearth.popquiz_backend.mapper;
import cn.edu.njust.hearth.popquiz_backend.entity.Course;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

@Mapper
public interface CourseMapper {
    @Select("SELECT COURSE_ID FROM COURSE_LISTENER WHERE USER_ID = #{uid}")
    public Set<Integer> findListenByUid(@Param("uid") Integer uid);

    @Select("SELECT * FROM SPEECHES WHERE COURSE_ID = #{cid}")
    public List<Speech> findSpeechesByCid(@Param("cid") Integer cid);

    @Select("SELECT * FROM SPEECHES WHERE ID = #{sid}")
    public Speech findSpeechById(@Param("sid") Integer sid);

    @Select("SELECT COURSE_ID FROM SPEECHES WHERE SPEAKER_ID = #{uid}")
    public Set<Integer> findCoursesByUid(@Param("uid") Integer uid);

    @Select("SELECT * FROM COURSES WHERE ID = #{cid}")
    public Course findByCid(@Param("cid") Integer cid);

    @Insert("INSERT INTO COURSES (title, description, ORGANIZER_ID) " +
            "VALUES (#{title}, #{description}, #{organizer_id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCourse(Course course);

    @Insert("INSERT INTO SPEECHES (title, SPEAKER_ID, COURSE_ID) " +
            "VALUES (#{title}, #{speaker_id}, #{course_id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSpeech(Speech speech);

    @Insert("INSERT INTO COURSE_LISTENER (COURSE_ID, USER_ID) " +
            "VALUES (#{cid}, #{uid})")
    int insertCourse_Listener(@Param("cid") int cid, @Param("uid") int uid);

    @Delete("DELETE FROM COURSE_LISTENER where COURSE_ID = #{cid} AND USER_ID = #{uid}")
    int deleteCourse_Listener(@Param("cid") int cid, @Param("uid") int uid);

    @Delete("DELETE FROM SPEECHES where COURSE_ID = #{cid} AND SPEAKER_ID = #{uid}")
    int deleteCourse_Teacher(@Param("cid") int cid, @Param("uid") int uid);

}
