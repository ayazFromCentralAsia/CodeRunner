package com.example.auth_service.service.Impl;

import com.example.auth_service.dto.ChangeUserInfoRequest;
import com.example.auth_service.dto.UserAuthRequest;
import com.example.auth_service.dto.UserInfoResponse;
import com.example.auth_service.exceptions.AlreadyExistsException;
import com.example.auth_service.service.AuthService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.keycloak.OAuth2Constants.CLIENT_ID;
import static org.keycloak.OAuth2Constants.CLIENT_SECRET;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final Keycloak keycloak;

    private static final String SERVER_URL = "http://localhost:8181";
    private static final String REALM = "master";
    private static final String CLIENT_ID = "auth-problem";
    private static final String CLIENT_SECRET = "yNBj0yEpEeZ59ZRwH3WnRpcyCeDOLw9t";

    @Override
    public String register(UserAuthRequest userDto) {
        log.info("Registering user: {}", userDto);
        RealmResource realm = keycloak.realm(REALM);

        if (!realm.users().search(userDto.getUsername()).isEmpty()) {
            throw new AlreadyExistsException("User with username already exists");
        }

        if (!realm.users().searchByEmail(userDto.getEmail(), true).isEmpty()) {
            throw new AlreadyExistsException("User with email already exists");
        }

        if (!Arrays.asList("USER", "ADMIN", "OPERATOR").contains(userDto.getRole())) {
            throw new RuntimeException("Invalid role");
        }

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setCredentials(Collections.singletonList(createPasswordCredential(userDto.getPassword())));

        Response response = realm.users().create(user);
        if (response.getStatus() != 201) {
            String responseBody = response.readEntity(String.class);
            log.info("Response status: {}, body: {}", response.getStatus(), responseBody);
            throw new RuntimeException("Failed to create user. Status: " + response.getStatus() + ", body: " + responseBody);
        }

        String userId = CreatedResponseUtil.getCreatedId(response);
        UserResource userResource = realm.users().get(userId);

        RoleRepresentation role = realm.roles().get(userDto.getRole()).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(role));

        log.info("User created successfully with role: {}", userDto.getRole());
        return login(userDto.getUsername(), userDto.getPassword());
    }

    public String login(String username, String password) {
        log.info("Logging in user: {}", username);
        try {
            KeycloakBuilder builder = KeycloakBuilder.builder()
                    .serverUrl(SERVER_URL)
                    .realm(REALM)
                    .clientId(CLIENT_ID)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(username)
                    .password(password);

            if (!CLIENT_SECRET.isEmpty()) {
                builder.clientSecret(CLIENT_SECRET);
            }

            Keycloak keycloakLogin = builder.build();
            AccessTokenResponse tokenResponse = keycloakLogin.tokenManager().getAccessToken();
            return tokenResponse.getToken();
        } catch (Exception e) {
            log.error("Failed to login user: {}", username, e);
            throw new RuntimeException("Failed to login user: " + e.getMessage());
        }
    }

    public UserInfoResponse getUserInfo(String token) {
        log.info("Getting user info for access token: {}", token);
        try {
            Keycloak keycloak = getKeycloakInstance();

            AccessToken accessToken = TokenVerifier.create(token, AccessToken.class).getToken();
            List<UserRepresentation> users = keycloak.realm(REALM).users().search(accessToken.getPreferredUsername());
            if (users.isEmpty()) {
                log.error("User not found for token: {}", token);
                throw new RuntimeException("User not found");
            }

            UserResource userResource = keycloak.realm(REALM).users().get(users.get(0).getId());
            UserRepresentation user = userResource.toRepresentation();

            UserInfoResponse userInfoResponse = new UserInfoResponse();
            userInfoResponse.setUsername(user.getUsername());
            userInfoResponse.setEmail(user.getEmail());

            String userId = user.getId();
            List<RoleRepresentation> roles = userResource.roles().realmLevel().listEffective();
            List<String> roleNames = roles.stream().map(RoleRepresentation::getName).collect(Collectors.toList());
            boolean isTrue = false;

            for (String roleName : roleNames) {
                if (!isTrue) {
                    if (roleName.equals("ADMIN")) {
                        userInfoResponse.setRoles("ADMIN");
                        isTrue = true;
                    } else if (roleName.equals("OPERATOR")) {
                        userInfoResponse.setRoles("OPERATOR");
                        isTrue = true;
                    } else if (roleName.equals("USER")) {
                        userInfoResponse.setRoles("USER");
                        isTrue = true;
                    }
                }
            }

            if (!isTrue) {
                userInfoResponse.setRoles("ROLE_NOT_FOUND");
            }
            return userInfoResponse;
        } catch (Exception e) {
            log.error("Failed to get user info", e);
            throw new RuntimeException("Failed to get user info: " + e.getMessage());
        }
    }

    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8181")
                .realm(REALM)
                .clientId("auth-problem")
                .clientSecret("yNBj0yEpEeZ59ZRwH3WnRpcyCeDOLw9t")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    public String resetPassword(String userId, String newPassword) {
        Keycloak keycloakAdmin = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .username("admin")
                .password("admin")
                .grantType("password")
                .build();

        try {
            UserResource userResource = keycloakAdmin.realm(REALM).users().get(userId);

            CredentialRepresentation newCredential = new CredentialRepresentation();
            newCredential.setType(CredentialRepresentation.PASSWORD);
            newCredential.setValue(newPassword);
            newCredential.setTemporary(false);

            userResource.resetPassword(newCredential);
            log.info("Password reset successfully for user: {}", userId);
            return "Password reset successfully";
        } catch (Exception e) {
            log.error("Failed to reset password for user: {}", userId, e);
            throw new RuntimeException("Failed to reset password: " + e.getMessage());
        }
    }


    private CredentialRepresentation createPasswordCredential(String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        return passwordCred;
    }
}
