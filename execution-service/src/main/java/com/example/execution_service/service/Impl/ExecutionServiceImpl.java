package com.example.execution_service.service.Impl;

import com.example.execution_service.dto.CodeMessage;
import com.example.execution_service.dto.ExecutionResult;
import com.example.execution_service.service.DockerService;
import com.example.execution_service.service.ExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ExecutionServiceImpl implements ExecutionService {
    private final KafkaTemplate<String, CodeMessage> kafkaTemplate;
    private final DockerService dockerService;

    @Override
    public String executeCode(CodeMessage code) {
        kafkaTemplate.send("code-topic", code);
        ExecutionResult result = dockerService.runCode(
                "java",
                "public class Main { public static void main(String[] args) { System.out.println(\"Hello from Java!\"); } }"
        );
        System.out.println("STDOUT:\n" + result.getStdout());
        System.out.println("STDERR:\n" + result.getStderr());
        System.out.println("Exit code: " + result.getExitCode());
        return "Code executed successfully";
    }

}
