package owt.boat_management.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import owt.boat_management.dto.LoginRequest;
import owt.boat_management.dto.RefreshTokenRequest;
import owt.boat_management.dto.RegisterRequest;
import owt.boat_management.dto.TokenPair;
import owt.boat_management.model.Role;
import owt.boat_management.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authService;

    @InjectMocks
    private AuthenticationController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private TokenPair tokenPair;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("my full name", "username", "password", Role.USER);
        loginRequest = new LoginRequest("username", "password");
        refreshTokenRequest = new RefreshTokenRequest("refreshToken");

        tokenPair = new TokenPair("accessToken", "refreshToken");
    }

    @Test
    void registerUser_ShouldReturnSuccessMessage() {
        // When
        doNothing().when(authService).registerUser(any(RegisterRequest.class));

        // Then
        ResponseEntity<?> response = authController.registerUser(registerRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(Map.of("message", "User created successfully"));

        // Verify
        verify(authService, times(1)).registerUser(registerRequest);
    }

    @Test
    void login_ShouldReturnTokenPair() {
        // When
        when(authService.login(any(LoginRequest.class))).thenReturn(tokenPair);

        // Then
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(tokenPair);

        // Verify
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void refreshToken_ShouldReturnNewTokenPair() {
        // When
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(tokenPair);

        // Then
        ResponseEntity<?> response = authController.refreshToken(refreshTokenRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(tokenPair);

        // Verify
        verify(authService, times(1)).refreshToken(refreshTokenRequest);
    }
}
