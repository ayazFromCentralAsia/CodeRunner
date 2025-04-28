package com.example.execution_service.controller;


import com.example.execution_service.dto.CodeMessage;
import com.example.execution_service.service.ExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/execution")
@RequiredArgsConstructor
@Tag(name = "Execution", description = "Execution related endpoints")
public class ExecutionController {
    private final ExecutionService executionService;

    @PostMapping("/code/submit")
    @Operation(summary = "Submit code for execution", description = "Submit code for execution and get the output")
    public String submitCode(@RequestBody CodeMessage code) {
        return executionService.executeCode(code);
    }
}
