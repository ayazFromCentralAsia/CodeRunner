package com.example.language_runtime_service.service;

import com.example.language_runtime_service.dto.CodeMessage;
import com.example.language_runtime_service.repository.CodeResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.CoderResult;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final CodeResultRepository coderResultRepository;

    @KafkaListener(topics = "code-topic", groupId = "code-executor-group")
    public void consumeJsonMessage(CodeMessage codeMessage) {

    }
}
