package com.example.language_runtime_service.dto;

import lombok.Data;

@Data
public class CodeMessage {
    private String id;
    private String language;
    private String code;
    private String message;
}
