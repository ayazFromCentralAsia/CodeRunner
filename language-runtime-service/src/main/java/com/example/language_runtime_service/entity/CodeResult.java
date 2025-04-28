package com.example.language_runtime_service.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "code_result")
@Data
@Setter
@Getter
public class CodeResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String language;
    private String code;
    private String message;

    private String output;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        SUCCESS,
        ERROR,
        TIMEOUT,
        COMPILE_ERROR,
        RUNTIME_ERROR
    }

}
