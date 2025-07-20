package cn.edu.njust.hearth.popquiz_backend.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliyunConfig {

    @Value("${aliyun.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.region:cn-beijing}")
    private String region;

    @Value("${aliyun.tingwu.appkey}")
    private String tingwuAppKey;

    // Getters
    public String getAccessKeyId() { return accessKeyId; }
    public String getAccessKeySecret() { return accessKeySecret; }
    public String getRegion() { return region; }
    public String getTingwuAppKey() { return tingwuAppKey; }
}
