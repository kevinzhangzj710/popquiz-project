package cn.edu.njust.hearth.popquiz_backend.service;

import cn.edu.njust.hearth.popquiz_backend.mapper.FilesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileService {
    final Path dirRoot = Paths.get("runtime/fileStore");

    FilesMapper filesMapper;


    @Autowired
    public void setFilesMapper(FilesMapper filesMapper) {
        this.filesMapper = filesMapper;
    }

    public FileService() {
        dirRoot.toFile().mkdirs();
    }

    public boolean appendTextContent(String fileName, String content) {
        File f1 = dirRoot.resolve(fileName).toFile();
        try {
            if (!f1.exists()) {
                f1.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try (FileWriter fw = new FileWriter(f1.getAbsolutePath(), true)) {
            fw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getTextContent(String fileName) {
        File f1 = dirRoot.resolve(fileName).toFile();
        try {
            return Files.readString(Path.of(f1.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 根据speech_id追加对应文件内容
     * @param speech_id
     * @param content
     */
    public void appendTextBySpeechID(Integer speech_id, String content) {
        String path = filesMapper.findPathBySpeechID(speech_id);
        if (path == null) {
            path = UUID.randomUUID().toString();
            filesMapper.insert(speech_id, path);
        }
        appendTextContent(path, content);
    }

    /**
     * 根据speech_id获取对应文件的内容
     * @param speech_id
     * @return 文件内容，没有时为空
     */
    public String getTextBySpeechID(Integer speech_id) {
        String path = filesMapper.findPathBySpeechID(speech_id);
        if (path == null) {
            return "";
        }
        return getTextContent(path);
    }
}
