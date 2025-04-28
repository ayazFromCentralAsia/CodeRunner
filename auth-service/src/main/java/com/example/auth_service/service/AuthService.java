package com.example.auth_service.service;

import com.example.auth_service.dto.ChangeUserInfoRequest;
import com.example.auth_service.dto.UserAuthRequest;
import com.example.auth_service.dto.UserInfoResponse;

public interface AuthService {
    String register(UserAuthRequest user);
    String login(String username, String password);
    UserInfoResponse getUserInfo(String accessToken);
    String resetPassword(String userId, String password);
}
