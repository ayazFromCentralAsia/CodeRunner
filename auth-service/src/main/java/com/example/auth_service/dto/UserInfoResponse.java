package com.example.auth_service.dto;


import com.example.auth_service.enumerate.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserInfoResponse {
    private String username;
    private String email;
    private String roles;
}
