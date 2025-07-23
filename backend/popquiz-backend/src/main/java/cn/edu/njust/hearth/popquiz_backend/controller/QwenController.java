package cn.edu.njust.hearth.popquiz_backend.controller;

import cn.edu.njust.hearth.popquiz_backend.mapper.FilesMapper;
import cn.edu.njust.hearth.popquiz_backend.mapper.QuestionMapper;
import cn.edu.njust.hearth.popquiz_backend.service.FileService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
//import com.openai.models.ChatCompletion;
//import com.openai.models.ChatCompletionCreateParams;
import cn.edu.njust.hearth.popquiz_backend.entity.Question;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
//import com.openai.models.ChatCompletionCreateParams.ResponseFormat;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class QwenController {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private FilesMapper filesMapper;

    @GetMapping("/generateQue")
    @Operation(summary = "根据speech_id，查找到录音、pdf、ppt文本，并生成题目", description = "生成成功返回question实体，失败返回空实体")
    public Question generateQuestion(@RequestParam int speech_id) throws IOException {
        String content_voice = fileService.getTextBySpeechID(speech_id);//获取音频文件
        List<String>urls = filesMapper.findFileBySpeechID(speech_id);
        String content_pdf = " ";
        for (String url : urls) {
            content_pdf = Files.readString(Path.of(url));
        }
        String content = content_voice +  content_pdf;
        //System.out.println("1111111111111111111111111111"+content+"22222222222222222222222222222");
        // 创建 OpenAI 客户端
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey("sk-6830bf28f64d429597df4d38efb63707")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
        // 构建系统提示词
        String systemPrompt = "你是一个题目生成助手，请严格按以下JSON格式生成选择题：\n" +
                "{\n" +
                "  \"question\": \"题面内容\",\n" +
                "  \"selection\": \"A.选项1 B.选项2 C.选项3 D.选项4\",\n" +
                "  \"answer\": \"正确答案字母\"\n" +
                "}\n" +
                "要求：\n" +
                "1. question不超过50字\n" +
                "2. selection包含四个用换行符分隔的选项（格式：A.xxx B.xxx C.xxx D.xxx）一个选项一行\n" +
                "3. answer只能是A/B/C/D中的一个字母\n" +
                "4. 题目主题：计算机网络基础知识";


        // 创建请求参数
        StructuredChatCompletionCreateParams<Question> params = ChatCompletionCreateParams.builder()
                .model("qwen-plus")
                .addSystemMessage(systemPrompt)
                .addUserMessage("请生成一道基于以下文字的选择题"+content)
                .responseFormat(Question.class)
                .build();

        Question question = client.chat().completions().create(params).choices().get(0).message().content().orElse(new Question());
         //System.out.println("11111111111111111111111111111"+client.chat().completions().create(params).choices().get(0).message().content()+"222222222222222222222222222222222222222");
        //System.out.println(question.question);
        question.setSpeech_id(speech_id);
        question.setStart_time(Timestamp.valueOf(LocalDateTime.now()));
        question.setEnd_time(Timestamp.valueOf(LocalDateTime.now()));

        questionMapper.insertQuestion(question);
        // 发送请求并获取响应
        //ChatCompletion chatCompletion = client.chat().completions().create(params);


        // 提取并打印 content 字段内容
//        List<ChatCompletion.Choice> choices = chatCompletion.choices();
//        if (!choices.isEmpty()) {
//            String content = choices.get(0).message().content().orElse("无响应内容");
//            System.out.println(content);
//        }



    return question;}

}
