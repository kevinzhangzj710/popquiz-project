package cn.edu.njust.hearth.popquiz_backend.service;

import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriber;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberListener;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import cn.edu.njust.hearth.popquiz_backend.service.FileService;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class RealtimeSpeechService {

    @Value("${aliyun.access-key-id}") private String accessKeyId;
    @Value("${aliyun.access-key-secret}") private String accessKeySecret;

    @Autowired  // 添加自动注入
    public FileService fileService;

//    public FileService  fileService;
//@Autowired
//public void setFileService(FileService fileService) {
//    this.fileService = fileService;
//}

    //static FileService fileService;

    private NlsClient client;
    private SpeechTranscriber transcriber;
    private TargetDataLine microphone;
    private volatile boolean isRecording = false;
    private final BlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>();

    /**
     * 初始化语音客户端
     */
    public void initClient() {
        // 使用智能语音交互(ISI)的token获取方式
        AccessToken accessToken = new AccessToken(accessKeyId, accessKeySecret);
        try {
            accessToken.apply();
            this.client = new NlsClient(accessToken.getToken());
        } catch (Exception e) {
            throw new RuntimeException("初始化语音客户端失败", e);
        }
    }

    /**
     * 启动实时语音转写
     */
    public void startRealtimeTranscription(int speech_id) {
        try {
            // 初始化客户端（如果未初始化）
            if (client == null) initClient();

            // 创建语音识别器
            transcriber = new SpeechTranscriber(client, new SpeechTranscriberListener() {
                public String text = " ";
                @Override
                public void onTranscriberStart(SpeechTranscriberResponse speechTranscriberResponse) {

                }

                @Override
                public void onSentenceBegin(SpeechTranscriberResponse speechTranscriberResponse) {

                }

                @Override
                public void onSentenceEnd(SpeechTranscriberResponse speechTranscriberResponse) {
                    text = text + speechTranscriberResponse.getTransSentenceText();
                    System.out.println("sentenceEnd,text: " + text);
                }

                @Override
                public void onTranscriptionResultChange(SpeechTranscriberResponse response) {
                    // 实时返回中间结果
                    System.out.println("中间结果: " + response.getTransSentenceText());
                }

                @Override
                public void onTranscriptionComplete(SpeechTranscriberResponse response) {
                    // 最终识别结果
                    System.out.println("最终text: " + text);
                    fileService.appendTextBySpeechID(speech_id,text);
                    System.out.println("最终结果: " + response.getTransSentenceText());
                }

                @Override
                public void onFail(SpeechTranscriberResponse response) {
                    System.err.println("识别失败: " + response.getStatusText());
                }

//                public void onMessage(SpeechTranscriberResponse response) {
//                    fileService.appendTextBySpeechID(speech_id,text);
//                }
            });

            // 配置识别参数
            transcriber.setAppKey("k3jt5KYIJKNxIBvh"); // ISI不需要AppKey，留空即可
            transcriber.setFormat(InputFormatEnum.PCM);
            transcriber.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            transcriber.setEnableIntermediateResult(true); // 启用中间结果
            transcriber.setEnablePunctuation(true); // 启用标点预测

            // 启动识别器
            transcriber.start();

            // 开始捕获麦克风输入
            startMicrophoneCapture();

            // 启动音频发送线程
            startAudioSendingThread();

        } catch (Exception e) {
            throw new RuntimeException("启动实时转写失败", e);
        }
    }

    /**
     * 启动麦克风捕获 - 使用纯Java Sound API
     */
    private void startMicrophoneCapture() throws LineUnavailableException {
        // 设置音频格式
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);

        // 获取麦克风
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("不支持音频格式: 16kHz, 16bit, 单声道");
        }

        microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();

        isRecording = true;
        System.out.println("开始捕获麦克风输入...");

        // 启动音频捕获线程
        new Thread(() -> {
            byte[] buffer = new byte[4096];
            while (isRecording) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    // 添加到队列
                    audioQueue.add(Arrays.copyOf(buffer, bytesRead));
                }
            }

            // 停止后关闭麦克风
            microphone.stop();
            microphone.close();
            System.out.println("麦克风已关闭");
        }).start();
    }

    /**
     * 启动音频发送线程
     */
    private void startAudioSendingThread() {
        new Thread(() -> {
            while (isRecording || !audioQueue.isEmpty()) {
                try {
                    // 从队列获取音频数据
                    byte[] audioData = audioQueue.poll(100, TimeUnit.MILLISECONDS);

                    if (audioData != null && transcriber != null) {
                        // 发送音频数据
                        transcriber.send(audioData);
                    }
                } catch (Exception e) {
                    System.err.println("音频发送失败: " + e.getMessage());
                }
            }
            System.out.println("音频发送线程已停止");
        }).start();
    }

    /**
     * 停止转写
     */
    public void stopTranscription() {
        isRecording = false;

        try {
            if (transcriber != null) {
                transcriber.stop();
            }
        } catch (Exception e) {
            System.err.println("停止转写失败: " + e.getMessage());
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
        System.out.println("转写已停止");
    }

    /**
     * 分段读取并转写 WAV 文件
     * @param filePath WAV 文件路径
     * @param chunkSize 每次读取的字节数（建议 4096 的倍数）
     */
    public void transcribeWavFileInChunks(int speech_id, String filePath, int chunkSize) {
        try {
            // 1. 初始化客户端
            if (client == null) initClient();

            // 2. 创建语音识别器
            transcriber = new SpeechTranscriber(client, createListener(speech_id));

            // 3. 配置识别参数
            transcriber.setAppKey("k3jt5KYIJKNxIBvh");//这里补充application.properties的阿里云的Appkey
            transcriber.setFormat(InputFormatEnum.PCM);
            transcriber.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            transcriber.setEnableIntermediateResult(true);
            transcriber.setEnablePunctuation(true);

            // 4. 启动识别器
            transcriber.start();

            // 5. 处理 WAV 文件
            processWavFile(filePath, chunkSize);
            //processWavFile(filePath, chunkSize);
            Thread.sleep(2000); // 等待2秒确保服务端处理完成

            // 6. 停止识别器
            transcriber.stop();
            transcriber.close();
        } catch (Exception e) {
            throw new RuntimeException("WAV 文件转写失败", e);
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    /**
     * 创建监听器
     */
    private SpeechTranscriberListener createListener(int speech_id) {
        //System.out.println("1111111111111"+fileService);
        return new SpeechTranscriberListener() {
            //public FileService fs = fileService;

            public FileService fs=fileService;
            public String text = " ";
            @Override
            public void onTranscriptionResultChange(SpeechTranscriberResponse response) {
                System.out.println("中间结果: " + response.getTransSentenceText());
            }

            @Override
            public void onTranscriptionComplete(SpeechTranscriberResponse response) {
                System.out.println("最终text: " + text);
                fs.appendTextBySpeechID(speech_id,text);
                System.out.println("最终结果: " + response.getTransSentenceText());
            }

            @Override
            public void onFail(SpeechTranscriberResponse response) {
                System.err.println("识别失败: " + response.getStatusText());
            }

            // 其他需要的方法可以留空
            @Override public void onTranscriberStart(SpeechTranscriberResponse r) {}
            @Override public void onSentenceBegin(SpeechTranscriberResponse r) {}
            @Override public void onSentenceEnd(SpeechTranscriberResponse r) {
                text = text + r.getTransSentenceText();
                System.out.println("sentenceEnd,text: " + text);
            }
        };
    }
    /**
     * 处理 WAV 文件（分段读取并发送）
     */
    private void processWavFile(String filePath, int chunkSize) throws Exception {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            // 1. 解析 WAV 文件头
            WavHeader header = parseWavHeader(file);
            System.out.println("WAV 文件信息: " + header);

            // 2. 验证音频格式
            if (!isSupportedFormat(header)) {
                throw new UnsupportedAudioFileException("不支持的音频格式");
            }

            // 3. 定位到音频数据开始位置
            file.seek(header.dataStart);

            // 4. 分段读取并发送音频数据
            byte[] buffer = new byte[chunkSize];
            int bytesRead;

            while ((bytesRead = file.read(buffer)) != -1) {
                // 如果读取不足一个完整块，只发送实际读取的部分
                if (bytesRead < buffer.length) {
                    byte[] partialBuffer = Arrays.copyOf(buffer, bytesRead);
                    transcriber.send(partialBuffer);
                } else {
                    transcriber.send(buffer);
                }

                // 模拟实时传输的延迟（可选）
                Thread.sleep(20);
            }

            System.out.println("WAV 文件处理完成");
        }
    }
    /**
     * 解析 WAV 文件头
     */
    private WavHeader parseWavHeader(RandomAccessFile file) throws IOException {
        WavHeader header = new WavHeader();

        // 读取前 12 个字节（RIFF 头）
        byte[] headerBytes = new byte[12];
        if (file.read(headerBytes) != 12) {
            throw new IOException("文件太小，不足以包含 WAV 头");
        }

        // 验证 RIFF 标识 - 使用更宽松的检查
        String riff = new String(headerBytes, 0, 4);
        if (!"RIFF".equals(riff)) {
            // 尝试检查大端序格式（某些专业设备可能使用）
            if ("RIFX".equals(riff)) {
                throw new IOException("大端序格式的 WAV 文件（RIFX）暂不支持");
            }
            throw new IOException("不是有效的 WAV 文件（缺少 RIFF 标识）");
        }

        // 文件大小（不包括 RIFF 和自身 4 字节）
        header.fileSize = (headerBytes[4] & 0xFF) |
                ((headerBytes[5] & 0xFF) << 8) |
                ((headerBytes[6] & 0xFF) << 16) |
                ((headerBytes[7] & 0xFF) << 24);

        // WAVE 标识
        String wave = new String(headerBytes, 8, 4);
        if (!"WAVE".equals(wave)) {
            throw new IOException("不是有效的 WAV 文件（缺少 WAVE 标识）");
        }

        // 查找 fmt 和 data 块
        boolean foundFmt = false;
        boolean foundData = false;

        while (!foundData) {
            // 读取块头
            byte[] chunkIdBytes = new byte[4];
            if (file.read(chunkIdBytes) != 4) {
                break; // 文件结束
            }

            String chunkId = new String(chunkIdBytes);
            int chunkSize = readInt(file);

            if ("fmt ".equals(chunkId)) {
                // 读取 fmt 块
                byte[] fmtData = new byte[Math.min(chunkSize, 16)];
                file.readFully(fmtData);

                // 解析音频格式信息
                header.audioFormat = (short) ((fmtData[0] & 0xFF) | (fmtData[1] & 0xFF) << 8);
                header.numChannels = (short) ((fmtData[2] & 0xFF) | (fmtData[3] & 0xFF) << 8);
                header.sampleRate = (fmtData[4] & 0xFF) |
                        ((fmtData[5] & 0xFF) << 8) |
                        ((fmtData[6] & 0xFF) << 16) |
                        ((fmtData[7] & 0xFF) << 24);
                header.byteRate = (fmtData[8] & 0xFF) |
                        ((fmtData[9] & 0xFF) << 8) |
                        ((fmtData[10] & 0xFF) << 16) |
                        ((fmtData[11] & 0xFF) << 24);
                header.blockAlign = (short) ((fmtData[12] & 0xFF) | (fmtData[13] & 0xFF) << 8);
                header.bitsPerSample = (short) ((fmtData[14] & 0xFF) | (fmtData[15] & 0xFF) << 8);

                // 跳过可能存在的额外格式信息
                if (chunkSize > 16) {
                    file.skipBytes(chunkSize - 16);
                }
                foundFmt = true;
            } else if ("data".equals(chunkId)) {
                // 找到数据块
                header.dataSize = chunkSize;
                header.dataStart = file.getFilePointer();
                foundData = true;
                // 不要跳过数据块，直接退出循环
            } else {
                // 跳过其他块（如 LIST, INFO, etc）
                file.skipBytes(chunkSize);
            }
        }

        if (!foundFmt) {
            throw new IOException("未找到必需的 'fmt ' 块");
        }

        if (!foundData) {
            throw new IOException("未找到必需的 'data' 块");
        }

        return header;
    }

    private boolean isSupportedFormat(WavHeader header) {
        // 检查是否为 PCM 格式
        if (header.audioFormat != 1) {
            System.err.println("只支持 PCM 格式的 WAV 文件");
            return false;
        }

        // 检查采样率
        if (header.sampleRate != 16000) {
            System.err.println("只支持 16kHz 采样率");
            return false;
        }

        // 检查位深度
        if (header.bitsPerSample != 16) {
            System.err.println("只支持 16 位深度");
            return false;
        }

        // 检查声道数
        if (header.numChannels != 1) {
            System.err.println("只支持单声道音频");
            return false;
        }

        return true;
    }

    /**
     * 从文件读取小端整数 (4 bytes)
     */
    /**
     * 从文件读取小端整数 (4 bytes)
     */
    private int readInt(RandomAccessFile file) throws IOException {
        byte[] bytes = new byte[4];
        file.readFully(bytes);
        return (bytes[0] & 0xFF) |
                ((bytes[1] & 0xFF) << 8) |
                ((bytes[2] & 0xFF) << 16) |
                ((bytes[3] & 0xFF) << 24);
    }

    /**
     * 从文件读取小端短整数 (2 bytes)
     */
    private short readShort(RandomAccessFile file) throws IOException {
        byte[] bytes = new byte[2];
        file.readFully(bytes);
        return (short) ((bytes[0] & 0xFF) | (bytes[1] << 8));
    }

    /**
     * WAV 文件头信息类
     */
    private static class WavHeader {
        int fileSize;        // 文件总大小
        short audioFormat;   // 音频格式 (1 = PCM)
        short numChannels;   // 声道数
        int sampleRate;      // 采样率
        int byteRate;        // 字节率
        short blockAlign;    // 块对齐
        short bitsPerSample; // 位深度
        int dataSize;        // 音频数据大小
        long dataStart;      // 音频数据起始位置

        @Override
        public String toString() {
            return String.format("PCM %dHz, %d-bit, %s, %d bytes",
                    sampleRate,
                    bitsPerSample,
                    numChannels == 1 ? "单声道" : "多声道",
                    dataSize);
        }
    }

}