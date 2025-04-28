package com.example.auth_service.dto;

import lombok.Data;

import java.util.List;


@Data
public class ChangeUserInfoRequest {
    private String username;
    private String email;
    private List<String> roles;
}
