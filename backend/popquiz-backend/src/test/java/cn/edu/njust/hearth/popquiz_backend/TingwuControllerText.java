package cn.edu.njust.hearth.popquiz_backend;
import cn.edu.njust.hearth.popquiz_backend.AudioConverter;
import cn.edu.njust.hearth.popquiz_backend.controller.TingwuController;
import cn.edu.njust.hearth.popquiz_backend.dto.RealtimeMeetingRequest;
import cn.edu.njust.hearth.popquiz_backend.service.RealtimeSpeechService;
import cn.edu.njust.hearth.popquiz_backend.service.TingwuService;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TingwuControllerText {

    private MockMvc mockMvc;

    @Mock
    private TingwuService tingwuService;

    @Mock
    private AudioConverter audioConverter;

    @Mock
    private RealtimeSpeechService realtimeSpeechService;

    private TingwuController tingwuController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tingwuController = new TingwuController(tingwuService);
        mockMvc = MockMvcBuilders.standaloneSetup(tingwuController).build();
    }

    // 测试创建实时会议成功的情况
    @Test
    public void testCreateRealtimeMeeting_Success() throws Exception {
        RealtimeMeetingRequest request = new RealtimeMeetingRequest();
        JSONObject result = new JSONObject();
        result.put("status", "success");

        when(tingwuService.submitRealtimeMeetingTask(any(RealtimeMeetingRequest.class))).thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tingwu/realtime-meeting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceLanguage\": \"cn\", \"format\": \"pcm\", \"sampleRate\": 16000}"))
                .andExpect(status().isOk());

        verify(tingwuService, times(1)).submitRealtimeMeetingTask(any(RealtimeMeetingRequest.class));
    }

    // 测试创建实时会议失败的情况
    @Test
    public void testCreateRealtimeMeeting_Failure() throws Exception {
        RealtimeMeetingRequest request = new RealtimeMeetingRequest();
        when(tingwuService.submitRealtimeMeetingTask(any(RealtimeMeetingRequest.class))).thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tingwu/realtime-meeting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceLanguage\": \"cn\", \"format\": \"pcm\", \"sampleRate\": 16000}"))
                .andExpect(status().isInternalServerError());

        verify(tingwuService, times(1)).submitRealtimeMeetingTask(any(RealtimeMeetingRequest.class));
    }


}