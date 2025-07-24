package cn.edu.njust.hearth.popquiz_backend.controller;
import cn.edu.njust.hearth.popquiz_backend.AudioConverter;
import cn.edu.njust.hearth.popquiz_backend.service.RealtimeSpeechService;
import com.alibaba.fastjson.JSONObject;
import cn.edu.njust.hearth.popquiz_backend.dto.RealtimeMeetingRequest;
import cn.edu.njust.hearth.popquiz_backend.service.TingwuService;
import cn.edu.njust.hearth.popquiz_backend.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/tingwu")
public class TingwuController {

    //private final TingwuService tingwuService;
    //private FileService fileService;

//    @Autowired
//    public TingwuController(TingwuService tingwuService) {
//        this.tingwuService = tingwuService;
//    }

//    @PostMapping("/realtime-meeting")
//    public ResponseEntity<?> createRealtimeMeeting(@RequestBody RealtimeMeetingRequest request) {
//        try {
//            JSONObject result = tingwuService.submitRealtimeMeetingTask(request);
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            JSONObject error = new JSONObject();
//            error.put("error", "Failed to create realtime meeting task");
//            error.put("message", e.getMessage());
//            return ResponseEntity.internalServerError().body(error);
//        }
//    }

    @Autowired
    public RealtimeSpeechService realtimeSpeechService;
    @Autowired
    public FileService fileService;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/uploadVoiceFile")
    @Operation(summary = "上传音频文件并解析",description = "不返回任何值")
    public void uploadVoiceFile(@RequestParam Integer speech_id, @RequestParam MultipartFile file) throws IOException, InterruptedException {
//        File f=new File("E:\\rec_1.wav");
//        file.transferTo(f);
        String uniqueName = "voice_" + System.currentTimeMillis() + "_" + ThreadLocalRandom.current().nextInt(1000);
        //File originalFile = new File("../runtime/uploadsVoice/" + uniqueName + ".webm");
        File originalFile = new File(uploadDir , uniqueName + ".webm");
        // 确保目录存在
        originalFile.getParentFile().mkdirs();
        file.transferTo(originalFile);

        //File of =new File("../runtime/uploadsVoice/" + uniqueName + ".wav");
        File of =new File(uploadDir ,   uniqueName + ".wav");
        AudioConverter.convert(originalFile,of);
        realtimeSpeechService.transcribeWavFileInChunks(speech_id, of.getAbsolutePath(),4096);
        //Thread.sleep(30000);
        //of.delete();
    }

    @GetMapping("/getTextofVoice")
    @Operation(summary = "获取某个speech所对应的音频txt文件",description = "返回一个字符串，表示解析音频文件之后的文字")
    public String getTextofVoice(@RequestParam int speech_id){
        return fileService.getTextBySpeechID(speech_id);
    }

}