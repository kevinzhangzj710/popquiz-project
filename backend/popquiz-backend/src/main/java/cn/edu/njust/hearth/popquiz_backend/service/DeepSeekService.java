package cn.edu.njust.hearth.popquiz_backend.service;

import cn.edu.njust.hearth.popquiz_backend.dto.DeepSeekRequest;
import cn.edu.njust.hearth.popquiz_backend.dto.DeepSeekResponse;
import cn.edu.njust.hearth.popquiz_backend.entity.Question;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.util.List;

@Service
public class DeepSeekService {
    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static class QuestionResponse {
        private String Questioncontent;
        private String A;
        private String B;
        private String C;
        private String D;
        private String Answer;

        // Getters and Setters (省略)
        public String getQuestioncontent() { return Questioncontent; }
        public String getA() { return A; }
        public String getB() { return B; }
        public String getC() { return C; }
        public String getD() { return D; }
        public String getAnswer() { return Answer; }
    }

    public Question generateQuestion(String content, int speechId) {
        // 1. 构造提示词（保持不变）
        String prompt = String.format("""
            你是一位资深教师，请根据以下教学内容生成1道题目：
            【教学材料】
            %s
            
            【要求】
            1. 包含1道包含四个选项的选择题
            2. 标注答案和解析
            3. 难度适中，考察核心知识点
            4. 严格按照以下JSON格式返回：
            {
                "Questioncontent":"题目内容",
                "A":"选项A",
                "B":"选项B",
                "C":"选项C",
                "D":"选项D",
                "Answer":"答案"
            }
            """, content.substring(0, Math.min(content.length(), 6000)));

        // 2. 构造请求体
        DeepSeekRequest request = new DeepSeekRequest();
        request.setModel("glm-4-plus");
        request.setMessages(List.of(new DeepSeekRequest.Message("user", prompt)));
        request.setTemperature(0.3);
        request.setMax_tokens(1500);

        // 3. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 4. 发送请求
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<DeepSeekRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<DeepSeekResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                DeepSeekResponse.class
        );

        // 5. 提取响应并转换
        String jsonResult = response.getBody().getChoices().get(0).getMessage().getContent();
        return parseResponse(jsonResult, speechId);
    }

    private Question parseResponse(String jsonResult, int speechId) {
        try {
            // 6. 解析JSON响应
            QuestionResponse response = objectMapper.readValue(jsonResult, QuestionResponse.class);

            // 7. 创建Question实体
            Question question = new Question();
            question.setQuestion(response.getQuestioncontent());

            // 8. 拼接选项字符串
            String selection = String.format(
                    "A. %s\nB. %s\nC. %s\nD. %s",
                    response.getA(), response.getB(), response.getC(), response.getD()
            );
            question.setSelection(selection);

            // 9. 设置答案（取第一个字符）
            question.setAnswer(response.getAnswer().charAt(0));

            // 10. 设置课程ID
            question.setSpeech_id(speechId);

            // 11. 设置时间（当前时间+1分钟）
            Timestamp now = new Timestamp(System.currentTimeMillis());
            question.setStart_time(now);

            // 结束时间 = 当前时间 + 1分钟
            long oneMinute = 60 * 1000;
            question.setEnd_time(new Timestamp(now.getTime() + oneMinute));

            return question;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse API response", e);
        }
    }
}
