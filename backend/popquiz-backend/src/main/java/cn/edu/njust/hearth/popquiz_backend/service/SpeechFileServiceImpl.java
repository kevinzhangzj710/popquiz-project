package cn.edu.njust.hearth.popquiz_backend.service;

import cn.edu.njust.hearth.popquiz_backend.entity.Speech_files;
import cn.edu.njust.hearth.popquiz_backend.mapper.SpeechFileMapper;
import cn.edu.njust.hearth.popquiz_backend.service.SpeechFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpeechFileServiceImpl implements SpeechFileService {

    private final SpeechFileMapper speechFileMapper;

    @Autowired
    public SpeechFileServiceImpl(SpeechFileMapper speechFileMapper) {
        this.speechFileMapper = speechFileMapper;
    }

    @Override
    @Transactional
    public void saveSpeechFile(Speech_files speechFile) {
        speechFileMapper.insertSpeechFile(speechFile);
    }
}
