package com.example.auth_service.controller;

import com.example.auth_service.dto.ChangeUserInfoRequest;
import com.example.auth_service.dto.UserAuthRequest;
import com.example.auth_service.dto.UserInfoResponse;
import com.example.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            description = "Register a new user with the given username, email, password, and role. Role must be either 'USER', 'OPERATOR' or 'ADMIN'. ",
            tags = {"Authentication API"}
    )
    public ResponseEntity<String> register(@RequestBody UserAuthRequest user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user",
            description = "Login a user with the given username and password. Returns an access token that can be used for subsequent requests.",
            tags = {"Authentication API"}
    )
    public ResponseEntity<String> login(@Parameter(description = "Username of the user") @RequestParam(value = "username", required = true) String username,
                                          @Parameter(description = "Password of the user") @RequestParam(value = "password", required = true) String password) {
        return ResponseEntity.ok(authService.login(username, password));
    }

    @PostMapping("/user-info")
    @Operation(summary = "Get user information",
            description = "Get user information with the given access token. Returns the user information in JSON format.",
            tags = {"Authentication API"}
    )
    public ResponseEntity<UserInfoResponse> getUserInfo(@Parameter(description = "Access token of the user") @RequestParam(value = "access_token", required = true) String accessToken) {
        return ResponseEntity.ok(authService.getUserInfo(accessToken));
    }

    @PostMapping("/{userId}/change-password")
    @Operation(summary = "Change user password",
            description = "Change user password with the given user ID and new password. Returns a message indicating whether the operation was successful or not.",
            tags = {"Authentication API"}
    )
    public ResponseEntity<String> changePassword(@PathVariable String userId, @RequestBody String newPassword) {
        authService.resetPassword(userId, newPassword);
        return ResponseEntity.ok("Пароль успешно изменён!");
    }
}