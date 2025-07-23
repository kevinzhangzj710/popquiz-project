package cn.edu.njust.hearth.popquiz_backend.controller;

import cn.edu.njust.hearth.popquiz_backend.entity.Speech_files;
import cn.edu.njust.hearth.popquiz_backend.service.SpeechFileService;
import cn.edu.njust.hearth.popquiz_backend.tool.FileTextExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class FileUploadController {

    private final SpeechFileService speechFileService;

    @Value("${file.storage.location}")
    private String storageLocation;

    public FileUploadController(SpeechFileService speechFileService) {
        this.speechFileService = speechFileService;
    }

    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("speech_id") int speechId) {

        // 1. 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                (!originalFilename.toLowerCase().endsWith(".pdf") &&
                        !originalFilename.toLowerCase().endsWith(".ppt") &&
                        !originalFilename.toLowerCase().endsWith(".pptx"))) {
            return "仅支持PDF/PPT文件";
        }

        try {
            // 2. 提取文本内容
            String textContent = FileTextExtractor.extractText(file);

            // 3. 生成唯一文件名
            String uuid = UUID.randomUUID().toString();
            String txtFilename = uuid + ".txt";
            String outputPath = storageLocation + File.separator + txtFilename;

            // 4. 保存文本文件
            FileTextExtractor.saveTextToFile(textContent, outputPath);

            // 5. 创建实体并保存到数据库
            Speech_files speechFile = new Speech_files();
            speechFile.setFilename(originalFilename);
            speechFile.setFilepath(outputPath);
            speechFile.setSpeech_id(speechId);

            speechFileService.saveSpeechFile(speechFile);

            return "文件处理成功! 保存路径: " + outputPath;

        } catch (IOException e) {
            e.printStackTrace();
            return "文件处理失败: " + e.getMessage();
        } catch (UnsupportedOperationException e) {
            return e.getMessage();
        }
    }
}