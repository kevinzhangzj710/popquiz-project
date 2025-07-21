package cn.edu.njust.hearth.popquiz_backend.tool;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileTextExtractor {

    public static String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new IOException("Invalid filename");

        if (filename.toLowerCase().endsWith(".pdf")) {
            return extractTextFromPDF(file);
        } else if (filename.toLowerCase().endsWith(".ppt") || filename.toLowerCase().endsWith(".pptx")) {
            return extractTextFromPPT(file);
        }
        throw new UnsupportedOperationException("Unsupported file type");
    }

    private static String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private static String extractTextFromPPT(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new IOException("Invalid filename");

        if (filename.toLowerCase().endsWith(".ppt")) {
            return extractFromPPT2003(file);
        } else {
            return extractFromPPT2007(file);
        }
    }

    // 修正后的PPT2003文本提取
    private static String extractFromPPT2003(MultipartFile file) throws IOException {
        try (HSLFSlideShow slideShow = new HSLFSlideShow(file.getInputStream())) {
            StringBuilder text = new StringBuilder();
            for (HSLFSlide slide : slideShow.getSlides()) {
                for (HSLFShape shape : slide.getShapes()) {
                    if (shape instanceof HSLFTextShape) {
                        HSLFTextShape textShape = (HSLFTextShape) shape;

//                        // 方法1：详细提取（保留格式信息）
//                        for (List<HSLFTextParagraph> paraList : textShape.getTextParagraphs()) {
//                            for (HSLFTextParagraph para : paraList) {
//                                for (HSLFTextRun run : para.getTextRuns()) {
//                                    text.append(run.getRawText());
//                                }
//                                text.append("\n");
//                            }
//                        }

                         //方法2：简化提取（推荐）
                         String shapeText = textShape.getText();
                         if (shapeText != null) {
                             text.append(shapeText).append("\n");
                         }
                    }
                }
            }
            return text.toString();
        }
    }

    // PPT2007+ 文本提取
    private static String extractFromPPT2007(MultipartFile file) throws IOException {
        try (XMLSlideShow slideShow = new XMLSlideShow(file.getInputStream())) {
            StringBuilder text = new StringBuilder();
            for (XSLFSlide slide : slideShow.getSlides()) {
                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        String shapeText = ((XSLFTextShape) shape).getText();
                        if (shapeText != null && !shapeText.trim().isEmpty()) {
                            text.append(shapeText).append("\n");
                        }
                    }
                }
            }
            return text.toString();
        }
    }

    public static File saveTextToFile(String content, String outputPath) throws IOException {
        File outputFile = new File(outputPath);
        Files.write(outputFile.toPath(), content.getBytes());
        return outputFile;
    }
}