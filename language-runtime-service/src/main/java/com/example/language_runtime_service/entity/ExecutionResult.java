package com.example.language_runtime_service.entity;

public class ExecutionResult {
    private final String stdout;
    private final String stderr;
    private final int exitCode;

    public ExecutionResult(String stdout, String stderr, int exitCode) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.exitCode = exitCode;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public int getExitCode() {
        return exitCode;
    }
}
