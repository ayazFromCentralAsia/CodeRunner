package com.example.execution_service.service;

import com.example.execution_service.dto.ExecutionResult;

public interface DockerService {
    ExecutionResult runCode(String language, String code);
}
