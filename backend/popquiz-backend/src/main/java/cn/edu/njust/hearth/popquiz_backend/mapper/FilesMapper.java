package cn.edu.njust.hearth.popquiz_backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FilesMapper {

    @Select("SELECT CONTENT_PATH from SPEECH_CONTENT where SPEECH_ID=#{speechId} limit 1")
    public String findPathBySpeechID(@Param("speechId") Integer speechId);

    @Insert("INSERT INTO SPEECH_CONTENT(speech_id, content_path) VALUES (#{speechId},#{contentPath})")
    public String insert(@Param("speechId") Integer speechId,@Param("contentPath")String contentPath);

}
