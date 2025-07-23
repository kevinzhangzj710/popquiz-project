package cn.edu.njust.hearth.popquiz_backend;

import cn.edu.njust.hearth.popquiz_backend.controller.FileUploadController;
import cn.edu.njust.hearth.popquiz_backend.entity.Speech_files;
import cn.edu.njust.hearth.popquiz_backend.service.SpeechFileService;
import cn.edu.njust.hearth.popquiz_backend.tool.FileTextExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FileUploadControllerTest {

    @Mock
    private SpeechFileService speechFileService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private FileUploadController fileUploadController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 手动设置 storageLocation
        ReflectionTestUtils.setField(fileUploadController, "storageLocation", "storage/location");
    }

    // 测试上传支持的文件类型
    // 测试上传支持的文件类型
    @Test
    void testHandleFileUpload_SupportedFileType() throws IOException {
        // 模拟文件信息
        when(file.getOriginalFilename()).thenReturn("test.pdf");

        try (MockedStatic<FileTextExtractor> mocked = Mockito.mockStatic(FileTextExtractor.class)) {
            mocked.when(() -> FileTextExtractor.extractText(file)).thenReturn("Test content");

            // 调用被测试方法
            String result = fileUploadController.handleFileUpload(file, 1);

            // 从实际结果中提取 UUID
            Pattern pattern = Pattern.compile("保存路径: storage/location[/\\\\]([a-f0-9-]+)\\.txt");
            Matcher matcher = pattern.matcher(result);
            if (matcher.find()) {
                String uuid = matcher.group(1);
                String txtFilename = uuid + ".txt";
                String outputPath = "storage/location/" + txtFilename;

                String expectedResult = "文件处理成功! 保存路径: " + outputPath;
                // 替换实际结果中的路径分隔符
                String normalizedResult = result.replace("\\", "/");
                // 验证结果
                assertEquals(expectedResult, normalizedResult); // 使用替换后的结果进行比较
                mocked.verify(() -> FileTextExtractor.extractText(file), times(1));
                verify(speechFileService, times(1)).saveSpeechFile(any(Speech_files.class));
            } else {
                throw new AssertionError("未找到有效的 UUID");
            }
        }
    }
//    @Test
//    void testHandleFileUpload_SupportedFileType() throws IOException {
//        // 模拟文件信息
//        when(file.getOriginalFilename()).thenReturn("test.pdf");
//
//        try (MockedStatic<FileTextExtractor> mocked = Mockito.mockStatic(FileTextExtractor.class)) {
//            mocked.when(() -> FileTextExtractor.extractText(file)).thenReturn("Test content");
//
//            // 调用被测试方法
//            String result = fileUploadController.handleFileUpload(file, 1);
//
//            // 从实际结果中提取 UUID
//            Pattern pattern = Pattern.compile("保存路径: storage/location[/\\\\]([a-f0-9-]+)\\.txt");
//            Matcher matcher = pattern.matcher(result);
//            if (matcher.find()) {
//                String uuid = matcher.group(1);
//                String txtFilename = uuid + ".txt";
//                String outputPath = "storage/location/" + txtFilename;
//
//                String expectedResult = "文件处理成功! 保存路径: " + outputPath;
//                // 替换实际结果中的路径分隔符
//                String normalizedResult = result.replace("\\", "/");
//                // 验证结果
//                assertEquals(expectedResult, result);
//                mocked.verify(() -> FileTextExtractor.extractText(file), times(1));
//                verify(speechFileService, times(1)).saveSpeechFile(any(Speech_files.class));
//            } else {
//                throw new AssertionError("未找到有效的 UUID");
//            }
//        }
//    }

    // 测试上传不支持的文件类型
    @Test
    void testHandleFileUpload_UnsupportedFileType() {
        // 模拟文件信息
        when(file.getOriginalFilename()).thenReturn("test.doc");

        // 调用被测试方法
        String result = fileUploadController.handleFileUpload(file, 1);

        // 验证结果
        assertEquals("仅支持PDF/PPT文件", result);
        verifyNoInteractions(speechFileService);
    }

    // 测试文本提取时抛出 IOException
    @Test
    void testHandleFileUpload_IOException() throws IOException {
        // 模拟文件信息
        when(file.getOriginalFilename()).thenReturn("test.pdf");

        try (MockedStatic<FileTextExtractor> mocked = Mockito.mockStatic(FileTextExtractor.class)) {
            mocked.when(() -> FileTextExtractor.extractText(file)).thenThrow(new IOException("Test exception"));

            // 调用被测试方法
            String result = fileUploadController.handleFileUpload(file, 1);

            // 验证结果
            assertEquals("文件处理失败: Test exception", result);
            mocked.verify(() -> FileTextExtractor.extractText(file), times(1));
            verifyNoInteractions(speechFileService);
        }
    }

    // 测试文本提取时抛出 UnsupportedOperationException
    @Test
    void testHandleFileUpload_UnsupportedOperationException() throws IOException {
        // 模拟文件信息
        when(file.getOriginalFilename()).thenReturn("test.pdf");

        try (MockedStatic<FileTextExtractor> mocked = Mockito.mockStatic(FileTextExtractor.class)) {
            mocked.when(() -> FileTextExtractor.extractText(file)).thenThrow(new UnsupportedOperationException("Unsupported file type"));

            // 调用被测试方法
            String result = fileUploadController.handleFileUpload(file, 1);

            // 验证结果
            assertEquals("Unsupported file type", result);
            mocked.verify(() -> FileTextExtractor.extractText(file), times(1));
            verifyNoInteractions(speechFileService);
        }
    }
}