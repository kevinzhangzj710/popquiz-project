package cn.edu.njust.hearth.popquiz_backend.controller;
import cn.edu.njust.hearth.popquiz_backend.entity.*;
import cn.edu.njust.hearth.popquiz_backend.mapper.CourseMapper;
import cn.edu.njust.hearth.popquiz_backend.mapper.FilesMapper;
import cn.edu.njust.hearth.popquiz_backend.mapper.QuestionMapper;
import cn.edu.njust.hearth.popquiz_backend.mapper.SpeechFileMapper;
import cn.edu.njust.hearth.popquiz_backend.requestBody.*;
import cn.edu.njust.hearth.popquiz_backend.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class QuestionController {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private SpeechFileMapper speechFileMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FilesMapper  filesMapper;
    @Autowired
    private CourseMapper courseMapper;

    @PostMapping("/createQuestion")
    @Operation(summary = "创建一个quiz",description = "创建成功返回quiz的id，失败返回-1")
    public int CreateQuestion(@RequestBody CreateQuestionRequest createQuestionRequest) {
        Question question = new Question();
        question.setQuestion(createQuestionRequest.getQuestion());
        question.setAnswer(createQuestionRequest.getAnswer());
        question.setSelection(createQuestionRequest.getSelection());
        question.setStart_time(createQuestionRequest.getStart_time());
        question.setEnd_time(createQuestionRequest.getEnd_time());
        question.setSpeech_id(createQuestionRequest.getSpeech_id());

        if(questionMapper.insertQuestion(question) == 1) {
            return question.id;
        }
        else return -1;
    }

    @GetMapping("/getQuestionById")
    @Operation(summary = "通过问题的id获取该问题",description = "获取成功返回问题实体，失败返回空实体")
    public Question getQuestionById(@RequestParam int question_id){
        Question question = questionMapper.findQuestionById(question_id);
        return question;
    }

    @GetMapping("getQuestionList")
    @Operation(summary = "通过speech_id获取该课时的所有quiz",description = "获取成功返回问题列表，失败返回空列表")
    public List<Question> getQuestionList(@RequestParam int speech_id){
        List<Question>questions = new ArrayList<>();
        Set<Integer>qids = questionMapper.findQuestionIdsBySpeechId(speech_id);
        for(Integer qid : qids) {
            Question question = questionMapper.findQuestionById(qid);
            questions.add(question);
        }
        return questions;
    }

    @PostMapping("/createSubmit")
    @Operation(summary = "学生提交某个quiz的答案",description = "提交成功返回submit_id,失败返回-1")
    public int CreateSubmit(@RequestBody CreateSubmitRequest createSubmitRequest){
        Submit submit = new Submit();
        submit.setQuestion_id(createSubmitRequest.getQuestion_id());
        submit.setAnswer(createSubmitRequest.getAnswer());
        submit.setUser_id(createSubmitRequest.getUser_id());

        if(questionMapper.insertSubmit(submit) == 1) {
            return submit.id;
        }
        else return -1;
    }

    @GetMapping("getSubmitId")
    @Operation(summary = "通过quiz的id和学生的id，获取到学生对于该quiz的submit_id",description = "提交成功返回submit_id,失败返回-1")
    public Integer GetSubmitId(@RequestParam int quiz_id,@RequestParam int user_id){
        Integer submit = questionMapper.getSubmitId(quiz_id, user_id);
        if(submit == null) return -1;
        else return submit;
    }
    @GetMapping("/getSubmitById")
    @Operation(summary = "通过submit的id获取该submit",description = "获取成功返回submit实体，失败返回空实体")
    public Submit getSubmitById(@RequestParam int submit_id){
        Submit submit = questionMapper.findSubmitById(submit_id);
        return submit;
    }

    @GetMapping("judgeSubmit")
    @Operation(summary = "判断学生对于某道题目的答案对错（默认学生只能提交一次答案）",description = "若答对返回true，答错返回false")
    public boolean judgeSubmit(@RequestParam int question_id,@RequestParam int user_id){
        char subAnswer = questionMapper.judgingAnswer(question_id, user_id);
        char realAnswer = questionMapper.findQuestionById(question_id).getAnswer();
        if(subAnswer == realAnswer) {return true;}
        else return false;
    }

    @GetMapping("getNumofRight")
    @Operation(summary = "获取在本次speech中，我答对了多少题",description = "返回答对题目的数量")
    public int getNumofRight(@RequestParam int speech_id,@RequestParam int user_id){
        int cnt = 0;
        Set<Integer>qids = questionMapper.findQuestionIdsBySpeechId(speech_id);
        for(Integer qid : qids) {
            if(judgeSubmit(qid,user_id)) {
                cnt++;
            }
        }
        return cnt;
    }

    @GetMapping("getRateofMine")
    @Operation(summary = "获取在本次speech中，我对所有题目的作答正确率是多少",description = "返回用户所答的正确率（用浮点数表示）")
    public float getRateofMine(@RequestParam int speech_id,@RequestParam int user_id){
        float rate = 0;
        float cntOfMine = getNumofRight(speech_id,user_id);
        float cntOfQuestion = getQuestionList(speech_id).size();
        rate = cntOfMine/cntOfQuestion;
        return rate;
    }

    @GetMapping("getRankofMine")
    @Operation(summary = "获取在本次speech中，我的正确率排名是多少",description = "返回用户的正确率排名（int类型）")
    public int getRankofMine(@RequestParam int speech_id,@RequestParam int user_id){
        List<Integer>uids = courseMapper.findListenersBySpeech_id(speech_id);
        int rank = 1;
        for(Integer uid : uids) {
            if(getRateofMine(speech_id,user_id)<getRateofMine(speech_id,uid))rank++;
        }
        return rank;
    }

    @GetMapping("getResultOfQuestion")
    @Operation(summary = "获取针对某个题目的具体信息",description = "返回该题目的具体信息，封装到一个实体类里面，字段含义依次为：多少人回答了、多少人没回答、多少人答对了、多少人答错了、共有多少人参加了speech、这道题的正确率是多少")
    public ResultOfQuestion getResultOfQuestion(@RequestParam int question_id) {
        float answerd_cnt = 0;
        float unanswerd_cnt = 0;
        float correct_cnt = 0;
        float wrong_cnt = 0;
        float total_cnt = 0;
        float accuracy_cnt = 0;
        List<Submit> submits = questionMapper.findSubmitsByQuestionId(question_id);
        for (Submit submit : submits) {
            if (submit.getAnswer() == 0) {
                unanswerd_cnt++;
            } else answerd_cnt++;

            if (judgeSubmit(question_id, submit.getUser_id())) {
                correct_cnt++;
            } else wrong_cnt++;

            total_cnt++;
        }
        accuracy_cnt = correct_cnt / total_cnt;

        ResultOfQuestion resultOfQuestion = new ResultOfQuestion();
        resultOfQuestion.setAnsweredCount(answerd_cnt);
        resultOfQuestion.setUnansweredCount(unanswerd_cnt);
        resultOfQuestion.setCorrectCount(correct_cnt);
        resultOfQuestion.setWrongCount(wrong_cnt);
        resultOfQuestion.setTotalCount(total_cnt);
        resultOfQuestion.setAccuracyRate(accuracy_cnt);

        return resultOfQuestion;
    }

    public static String getTextContent(String urlString) {
        StringBuilder content = new StringBuilder();

        try {
            // 创建URL对象并打开连接
            URL url = new URL(urlString);

            // 使用BufferedReader读取内容（自动处理字符编码）
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {

                String line;
                // 逐行读取内容
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n"); // 保留换行符
                }
            }
        } catch (IOException e) {
            // 异常处理（可替换为自定义逻辑）
            System.err.println("读取文件失败: " + e.getMessage());
            return "ERROR: " + e.getMessage(); // 返回错误信息
        }

        return content.toString();
    }
//    @GetMapping("/generateQuestion")
//    @Operation(summary = "根据speech_id，查找到录音、pdf、ppt文本，并生成题目",description = "生成成功返回question实体，失败返回空实体")
//    public Question generateQuestion(@RequestParam int speech_id) throws IOException {
//        String content_voice = fileService.getTextBySpeechID(speech_id);//获取音频文件
//        List<String>urls = filesMapper.findFileBySpeechID(speech_id);
//        String content_pdf = " ";
//        for (String url : urls) {
//            content_pdf = Files.readString(Path.of(url));
//        }
//        //System.out.println(content_pdf);
//        Question question = deepSeekService.generateQuestion(content_pdf, speech_id);
//        return question;
//    }

//    @GetMapping("/generateQuestion_Bypdf")
//    @Operation(summary = "根据speech_id，查找到pdf、ppt文本，并生成题目",description = "生成成功返回question实体，失败返回空实体")
//    public Question generateQuestionBypdf(@RequestParam int speech_id) {
//        String content = fileService.getTextBySpeechID(speech_id);
//        Question question = deepSeekService.generateQuestion(content, speech_id);
//        return question;
//    }

}
