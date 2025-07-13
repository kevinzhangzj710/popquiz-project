package cn.edu.njust.hearth.popquiz_backend.controller;
import cn.edu.njust.hearth.popquiz_backend.entity.Course;
import cn.edu.njust.hearth.popquiz_backend.mapper.CourseMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseController {
    @Autowired
    private CourseMapper courseMapper;
    @GetMapping("/getListeningCourse")
    @Operation(summary = "获取该用户所听的课",description = "获取成功返回课程列表，失败返回空列表")
    public List<Course> getListeningCourse(@RequestParam Integer uid) {
        List<Course>courses = new ArrayList<>();
        List<Integer>courseIds = courseMapper.findListenByUid(uid);
        if (courseIds.size() > 0) {
            for (int cid : courseIds)
            {
                Course course = courseMapper.findByCid(cid);
                courses.add(course);
            }
        }
        return courses;
    }

    @GetMapping("/getSpeechCourse")
    @Operation(summary = "获取该用户所讲的课",description = "获取成功返回课程列表，失败返回空列表")
    public List<Course> getSpeechCourse(@RequestParam Integer uid) {
        List<Course>courses = new ArrayList<>();
        List<Integer>courseIds = courseMapper.findSpeechByUid(uid);
        if (courseIds.size() > 0) {
            for (int cid : courseIds)
            {
                Course course = courseMapper.findByCid(cid);
                courses.add(course);
            }
        }
        return courses;
    }
}
