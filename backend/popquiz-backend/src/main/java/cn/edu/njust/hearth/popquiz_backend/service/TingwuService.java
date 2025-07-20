package cn.edu.njust.hearth.popquiz_backend.service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import cn.edu.njust.hearth.popquiz_backend.config.AliyunConfig;
import cn.edu.njust.hearth.popquiz_backend.dto.RealtimeMeetingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TingwuService {

    private final AliyunConfig aliyunConfig;

    @Autowired
    public TingwuService(AliyunConfig aliyunConfig) {
        this.aliyunConfig = aliyunConfig;
    }

    public JSONObject submitRealtimeMeetingTask(RealtimeMeetingRequest requestParams) throws ClientException {
        String domain = "tingwu." + aliyunConfig.getRegion() + ".aliyuncs.com";
        CommonRequest request = createCommonRequest(
                domain,
                "2023-09-30",
                ProtocolType.HTTPS,
                MethodType.PUT,
                "/openapi/tingwu/v2/tasks"
        );

        request.putQueryParameter("type", "realtime");

        JSONObject root = new JSONObject();
        root.put("AppKey", aliyunConfig.getTingwuAppKey());

        JSONObject input = new JSONObject();
        input.fluentPut("SourceLanguage", requestParams.getSourceLanguage())
                .fluentPut("Format", requestParams.getFormat())
                .fluentPut("SampleRate", requestParams.getSampleRate())
                .fluentPut("TaskKey", "task" + System.currentTimeMillis());
        root.put("Input", input);

        JSONObject parameters = initRequestParameters(requestParams);
        root.put("Parameters", parameters);

        request.setHttpContent(root.toJSONString().getBytes(), "utf-8", FormatType.JSON);

        DefaultProfile profile = DefaultProfile.getProfile(
                aliyunConfig.getRegion(),
                aliyunConfig.getAccessKeyId(),
                aliyunConfig.getAccessKeySecret()
        );

        IAcsClient client = new DefaultAcsClient(profile);
        CommonResponse response = client.getCommonResponse(request);

        return JSONObject.parseObject(response.getData());
    }

    private JSONObject initRequestParameters(RealtimeMeetingRequest requestParams) {
        JSONObject parameters = new JSONObject();

        // 语音识别配置
        JSONObject transcription = new JSONObject();
        transcription.put("DiarizationEnabled", requestParams.isDiarizationEnabled());

        if(requestParams.isDiarizationEnabled()) {
            JSONObject diarization = new JSONObject();
            diarization.put("SpeakerCount", requestParams.getSpeakerCount());
            transcription.put("Diarization", diarization);
        }
        parameters.put("Transcription", transcription);

        // 翻译配置
        parameters.put("TranslationEnabled", requestParams.isTranslationEnabled());
        if(requestParams.isTranslationEnabled()) {
            JSONObject translation = new JSONObject();
            JSONArray langArray = new JSONArray();
            for(String lang : requestParams.getTargetLanguages()) {
                langArray.add(lang);
            }
            translation.put("TargetLanguages", langArray);
            parameters.put("Translation", translation);
        }

        // 摘要配置
        parameters.put("SummarizationEnabled", requestParams.isSummarizationEnabled());
        if(requestParams.isSummarizationEnabled()) {
            JSONObject summarization = new JSONObject();
            JSONArray types = new JSONArray();
            for(String type : requestParams.getSummarizationTypes()) {
                types.add(type);
            }
            summarization.put("Types", types);
            parameters.put("Summarization", summarization);
        }

        // 其他配置（根据需要添加）
        parameters.put("AutoChaptersEnabled", false);
        parameters.put("MeetingAssistanceEnabled", false);
        parameters.put("TextPolishEnabled", false);

        return parameters;
    }

    private CommonRequest createCommonRequest(String domain, String version,
                                              ProtocolType protocolType,
                                              MethodType method, String uri) {
        CommonRequest request = new CommonRequest();
        request.setSysDomain(domain);
        request.setSysVersion(version);
        request.setSysProtocol(protocolType);
        request.setSysMethod(method);
        request.setSysUriPattern(uri);
        request.setHttpContentType(FormatType.JSON);
        return request;
    }
}