package cn.edu.njust.hearth.popquiz_backend.controller;

import cn.edu.njust.hearth.popquiz_backend.entity.Course;
import cn.edu.njust.hearth.popquiz_backend.entity.Course_listener;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech;
import cn.edu.njust.hearth.popquiz_backend.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    public UserMapper userMapper;
    @GetMapping("/getTypeofUser")
    @Operation(summary = "对于给定SpeechID和UserID，判断是不是学生",description = "是学生返回1，是演讲者或者组织者返回0，都不是返回-1")
    public int GetTypeofUser(@RequestParam int user_id, @RequestParam int speech_id) {
        System.out.println(user_id);
        System.out.println(speech_id);
        Course_listener course_listener = userMapper.findCour_Listener(user_id, speech_id);
        System.out.println(course_listener);
        Course course = userMapper.findCourseById(user_id, speech_id);
        Speech speech = userMapper.findSpeechById(user_id, speech_id);
        if(course_listener != null){return 1;}
        else if(speech != null||course != null){return 0;}
        else return -1;
    }

}
