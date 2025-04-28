package com.example.execution_service.service.Impl;

import com.example.execution_service.dto.ExecutionResult;
import com.example.execution_service.service.DockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;


@Service
public class DockerServiceImpl implements DockerService {

    public ExecutionResult runCode(String language, String code) {
        String image = getImageForLanguage(language);
        String command = getCommandForLanguage(language, code);
        return executeInDocker(image, command);
    }

    private String getImageForLanguage(String language) {
        switch (language.toLowerCase()) {
            case "python": return "python:3.10";
            case "java": return "eclipse-temurin:17-jdk-alpine";
            case "cpp": return "gcc:latest";
            case "javascript": return "node:20-alpine"; // Node.js
            default: throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private String getCommandForLanguage(String language, String code) {
        switch (language.toLowerCase()) {
            case "python":
                return "python3 -c \"" + code.replace("\"", "\\\"") + "\"";
            case "java":
                return "sh -c \"echo '" + code.replace("'", "'\\''") + "' > Main.java && javac Main.java && java Main\"";
            case "cpp":
                return "sh -c \"echo '" + code.replace("'", "'\\''") + "' > main.cpp && g++ main.cpp -o main && ./main\"";
            case "javascript":
                return "node -e \"" + code.replace("\"", "\\\"") + "\"";
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private ExecutionResult executeInDocker(String image, String command) {
        try {

            ProcessBuilder builder = new ProcessBuilder(
                    "docker", "run", "--rm", "-i", "--network", "none", image, "sh", "-c", command
            );

            Process process = builder.start();

            // Чтение stdout
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder stdout = new StringBuilder();
            String line;
            while ((line = stdoutReader.readLine()) != null) {
                stdout.append(line).append("\n");
            }

            // Чтение stderr
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder stderr = new StringBuilder();
            while ((line = stderrReader.readLine()) != null) {
                stderr.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            return new ExecutionResult(stdout.toString().trim(), stderr.toString().trim(), exitCode);
        } catch (Exception e) {
            return new ExecutionResult("", "Exception: " + e.getMessage(), -1);
        }
    }
}