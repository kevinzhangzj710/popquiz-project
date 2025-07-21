package cn.edu.njust.hearth.popquiz_backend.mapper;

import cn.edu.njust.hearth.popquiz_backend.entity.Speech_content;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech_files;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SpeechFileMapper {
    @Insert("INSERT INTO speech_files (filename, filepath, speech_id) " +
            "VALUES (#{filename}, #{filepath}, #{speech_id})")
    void insertSpeechFile(Speech_files speechFile);

    @Select("SELECT * FROM SPEECH_CONTENT WHERE SPEECH_ID = #{speech_id}")
    Speech_content getSpeechContent(int speech_id);
}
