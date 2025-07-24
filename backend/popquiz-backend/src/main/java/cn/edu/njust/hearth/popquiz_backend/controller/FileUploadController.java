package cn.edu.njust.hearth.popquiz_backend.controller;

import cn.edu.njust.hearth.popquiz_backend.entity.Speech_files;
import cn.edu.njust.hearth.popquiz_backend.mapper.FilesMapper;
import cn.edu.njust.hearth.popquiz_backend.service.SpeechFileService;
import cn.edu.njust.hearth.popquiz_backend.tool.FileTextExtractor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class FileUploadController {

    private final SpeechFileService speechFileService;
    private final FilesMapper filesMapper;

    @Value("${file.storage.location}")
    private String storageLocation;

    public FileUploadController(SpeechFileService speechFileService, FilesMapper filesMapper) {
        this.speechFileService = speechFileService;
        this.filesMapper = filesMapper;
    }

    @PostMapping("/upload")
    @Operation(summary = "上传speech的pdf和ppt文件",description = "上传成功返回保存路径，失败返回错误信息")
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

            File storageDir = new File(storageLocation);
            if (!storageDir.exists()) {
                boolean created = storageDir.mkdirs();
                if (!created) {
                    return "无法创建存储目录: " + storageLocation;
                }
            }

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

    @GetMapping("getAllUploads")
    @Operation(summary = "获取该speech上传的所有文件名",description = "获取成功返回文件名列表，失败返回空列表")
    public List<String> getAllUploads(@RequestParam int speech_id){
        List<String>names = filesMapper.findFileNamesBySpeechID(speech_id);
        return names;
    }

}