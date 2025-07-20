package cn.edu.njust.hearth.popquiz_backend.dto;
public class RealtimeMeetingRequest {
    private String sourceLanguage = "cn";
    private String format = "pcm";
    private int sampleRate = 16000;
    private boolean diarizationEnabled = true;
    private int speakerCount = 2;
    private boolean translationEnabled = true;
    private String[] targetLanguages = {"en"};
    private boolean summarizationEnabled = true;
    private String[] summarizationTypes = {"Paragraph", "Conversational", "QuestionsAnswering", "MindMap"};

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public boolean isDiarizationEnabled() {
        return diarizationEnabled;
    }

    public void setDiarizationEnabled(boolean diarizationEnabled) {
        this.diarizationEnabled = diarizationEnabled;
    }

    public int getSpeakerCount() {
        return speakerCount;
    }

    public void setSpeakerCount(int speakerCount) {
        this.speakerCount = speakerCount;
    }

    public boolean isTranslationEnabled() {
        return translationEnabled;
    }

    public void setTranslationEnabled(boolean translationEnabled) {
        this.translationEnabled = translationEnabled;
    }

    public String[] getTargetLanguages() {
        return targetLanguages;
    }

    public void setTargetLanguages(String[] targetLanguages) {
        this.targetLanguages = targetLanguages;
    }

    public boolean isSummarizationEnabled() {
        return summarizationEnabled;
    }

    public void setSummarizationEnabled(boolean summarizationEnabled) {
        this.summarizationEnabled = summarizationEnabled;
    }

    public String[] getSummarizationTypes() {
        return summarizationTypes;
    }

    public void setSummarizationTypes(String[] summarizationTypes) {
        this.summarizationTypes = summarizationTypes;
    }
// Getters and Setters
    // 省略getter/setter方法，实际项目中请添加
}
