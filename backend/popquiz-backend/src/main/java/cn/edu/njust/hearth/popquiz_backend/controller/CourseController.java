package cn.edu.njust.hearth.popquiz_backend.controller;
import cn.edu.njust.hearth.popquiz_backend.entity.Course;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech;
import cn.edu.njust.hearth.popquiz_backend.mapper.CourseMapper;
import cn.edu.njust.hearth.popquiz_backend.requestBody.AddCourseRequest;
import cn.edu.njust.hearth.popquiz_backend.requestBody.CreateCourseRequest;
import cn.edu.njust.hearth.popquiz_backend.requestBody.CreateSpeechRequest;
import cn.edu.njust.hearth.popquiz_backend.requestBody.DeleteCourseRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class CourseController {
    @Autowired
    private CourseMapper courseMapper;
    @GetMapping("/getListeningCourse")
    @Operation(summary = "获取该用户所听的课程（不包含课时）",description = "获取成功返回课程列表，失败返回空列表")
    public List<Course> getListeningCourse(@RequestParam Integer uid) {
        List<Course>courses = new ArrayList<>();
        Set<Integer>courseIds = courseMapper.findListenByUid(uid);
        if (courseIds.size() > 0) {
            for (int cid : courseIds)
            {
                Course course = courseMapper.findByCid(cid);
                courses.add(course);
            }
        }
        return courses;
    }

    @GetMapping("/getSpeeches")
    @Operation(summary = "获取某课程的所有课时",description = "获取成功返回课时列表，失败返回空列表")
    public List<Speech> getSpeeches(@RequestParam Integer courseid) {
        List<Speech>speeches = courseMapper.findSpeechesByCid(courseid);
        return speeches;
    }

    @GetMapping("/getTeachingCourse")
    @Operation(summary = "获取该用户所讲的课程（不包括课时）",description = "获取成功返回课程列表，失败返回空列表")
    public List<Course> getTeachingCourse(@RequestParam Integer uid) {
        List<Course>courses = new ArrayList<>();
        Set<Integer> courseIds = courseMapper.findCoursesByUid(uid);
        if (courseIds.size() > 0) {
            for (int cid : courseIds)
            {
                Course course = courseMapper.findByCid(cid);
                courses.add(course);
            }
        }
        return courses;
    }

    @PostMapping("/createCourse")
    @Operation(summary = "创建课程",description = "创建成功返回课程id,失败返回-1")
    public int createCourse(@RequestBody CreateCourseRequest courseRequest){
        Course course = new Course();
        course.setOrganizer_id(courseRequest.organizer_id());
        if (!StringUtils.hasText(courseRequest.title())) {
            return -1;
        }
        else {
            course.setTitle(courseRequest.title());
        }
        if(courseRequest.description() == null) {
            course.setDescription("");
        }
        else{
            course.setDescription(courseRequest.description());
        }

        if (courseMapper.insertCourse(course) == 1) {
            // 4. 此时 course 对象的 id 已被 MyBatis 自动填充
            return course.id(); // 返回自增 ID
        } else {
            return -1;
        }
    }

    @PostMapping("/createSpeech")
    @Operation(summary = "创建课时",description = "创建成功返回课时id,失败返回-1")
    public int createSpeech(@RequestBody CreateSpeechRequest speechRequest){
        Speech speech = new Speech();
        if (!StringUtils.hasText(speechRequest.title())) {
            return -1;
        }
        else {
            speech.setTitle(speechRequest.title());
        }
        speech.setSpeaker_id(speechRequest.speaker_id());
        speech.setCourse_id(speechRequest.course_id());
        if (courseMapper.insertSpeech(speech) == 1) {
            // 4. 此时 speech 对象的 id 已被 MyBatis 自动填充
            return speech.id(); // 返回自增 ID
        } else {
            return -1;
        }
    }

    @PostMapping("/addCourse")
    @Operation(summary = "学生添加课程",description = "成功返回1，失败返回0")
    public int addCourse(@RequestBody AddCourseRequest addCourseRequest){
        int cid = addCourseRequest.course_id();
        int uid = addCourseRequest.uid();
        return courseMapper.insertCourse_Listener(cid, uid);
    }

    @PostMapping("/deleteCourse_Listen")
    @Operation(summary = "学生删除所听的课程",description = "成功/失败不返回信息")
    public void deleteCourse_Listen(@RequestBody DeleteCourseRequest deleteCourseRequest){
        int cid = deleteCourseRequest.course_id();
        int uid = deleteCourseRequest.uid();
        courseMapper.deleteCourse_Listener(cid, uid);
    }

    @PostMapping("/deleteCourse_Teach")
    @Operation(summary = "老师删除所讲的课程",description = "成功返回一个>0的数表示删掉的记录数，失败返回0")
    public int deleteCourse_Teach(@RequestBody DeleteCourseRequest deleteCourseRequest){
        int cid = deleteCourseRequest.course_id();
        int uid = deleteCourseRequest.uid();
        return courseMapper.deleteCourse_Teacher(cid, uid);
    }
}
