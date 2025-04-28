package com.example.execution_service.service;

import com.example.execution_service.dto.CodeMessage;

public interface ExecutionService {
    String executeCode(CodeMessage code);
}
