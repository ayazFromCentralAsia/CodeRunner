package com.example.auth_service.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Model for user authentication request")
public class UserAuthRequest {

    @Schema(description = "Username", example = "john_doe")
    @NotBlank
    private String username;

    @Schema(description = "Email address", example = "john@example.com")
    @Email
    private String email;

    @Schema(description = "User password", example = "password123")
    @NotBlank
    private String password;

    @Schema(description = "User role", example = "USER")
    private String role;
}
