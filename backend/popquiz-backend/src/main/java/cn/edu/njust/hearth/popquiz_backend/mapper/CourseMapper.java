package cn.edu.njust.hearth.popquiz_backend.mapper;
import cn.edu.njust.hearth.popquiz_backend.entity.Course;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
@Mapper
public interface CourseMapper {
    @Select("SELECT COURSE_ID FROM COURSE_LISTENER WHERE USER_ID = #{uid}")
    public List<Integer> findListenByUid(@Param("uid") Integer uid);

    @Select("SELECT COURSE_ID FROM SPEECHES WHERE SPEAKER_ID = #{uid}")
    public List<Integer> findSpeechByUid(@Param("uid") Integer uid);

    @Select("SELECT * FROM COURSES WHERE ID = #{cid}")
    public Course findByCid(@Param("cid") Integer cid);


}
