package cn.edu.njust.hearth.popquiz_backend.controller;
import cn.edu.njust.hearth.popquiz_backend.AudioConverter;
import cn.edu.njust.hearth.popquiz_backend.service.RealtimeSpeechService;
import com.alibaba.fastjson.JSONObject;
import cn.edu.njust.hearth.popquiz_backend.dto.RealtimeMeetingRequest;
import cn.edu.njust.hearth.popquiz_backend.service.TingwuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/tingwu")
public class TingwuController {

    private final TingwuService tingwuService;

    @Autowired
    public TingwuController(TingwuService tingwuService) {
        this.tingwuService = tingwuService;
    }

    @PostMapping("/realtime-meeting")
    public ResponseEntity<?> createRealtimeMeeting(@RequestBody RealtimeMeetingRequest request) {
        try {
            JSONObject result = tingwuService.submitRealtimeMeetingTask(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("error", "Failed to create realtime meeting task");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/upload")
    public void upload(@RequestParam Integer speech_id, @RequestParam MultipartFile file) throws IOException, InterruptedException {
        File f=new File("E:\\rec_1.wav");
        file.transferTo(f);
        File of = File.createTempFile("output_",".wav");
        AudioConverter.convert(f,of);
        RealtimeSpeechService rts=new RealtimeSpeechService();
        rts.transcribeWavFileInChunks(of.getAbsolutePath(),4096);
        Thread.sleep(30000);
        of.delete();
        //传到数据库
    }
}