package owt.boat_management.service;

import owt.boat_management.dto.LoginRequest;
import owt.boat_management.dto.RefreshTokenRequest;
import owt.boat_management.dto.RegisterRequest;
import owt.boat_management.dto.TokenPair;
import owt.boat_management.model.Role;
import owt.boat_management.model.Token;
import owt.boat_management.model.User;
import owt.boat_management.repository.TokenRepository;
import owt.boat_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Token token;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .username("testuser")
                .fullName("Test User")
                .password("password")
                .role(Role.USER)
                .build();
    }

    @Test
    void registerUser_WhenUserDoesNotExist_ShouldSaveNewUser() {
        // When
        RegisterRequest registerRequest = new RegisterRequest("testuser", "Test User", "password", Role.USER);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);

        // Then
        authenticationService.registerUser(registerRequest);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_WhenUserExists_ShouldThrowException() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("test user", "testuser", "password", Role.USER);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Then & Assert
        assertThrows(IllegalArgumentException.class, () -> authenticationService.registerUser(registerRequest));
    }

    @Test
    void login_WhenCredentialsAreValid_ShouldReturnTokenPair() {
        // When
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenService.generateTokenPair(authentication)).thenReturn(new TokenPair("accessToken", "refreshToken"));
        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(user));

        // Then
        TokenPair tokenPair = authenticationService.login(loginRequest);

        // Assert
        assertNotNull(tokenPair);
        assertEquals("accessToken", tokenPair.accessToken());
        assertEquals("refreshToken", tokenPair.refreshToken());
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void login_WhenAuthenticationFails_ShouldThrowException() {
        // When
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        // Then & Assert
        assertThrows(RuntimeException.class, () -> authenticationService.login(loginRequest));
    }

    @Test
    void refreshToken_WhenValidRefreshToken_ShouldReturnNewTokenPair() {
        // When
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("validRefreshToken");
        when(tokenService.isRefreshToken("validRefreshToken")).thenReturn(true);
        when(tokenService.extractUsernameFromToken("validRefreshToken")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(tokenService.generateAccessToken(any())).thenReturn("newAccessToken");
        when(tokenRepository.findByRefreshToken("validRefreshToken")).thenReturn(java.util.Optional.of(token));
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.of(new User()));

        // Act
        TokenPair tokenPair = authenticationService.refreshToken(refreshTokenRequest);

        // Assert
        assertNotNull(tokenPair);
        assertEquals("newAccessToken", tokenPair.accessToken());
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void refreshToken_WhenInvalidRefreshToken_ShouldThrowException() {
        // When
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("invalidRefreshToken");
        when(tokenService.isRefreshToken("invalidRefreshToken")).thenReturn(false);

        // Then & Assert
        assertThrows(IllegalArgumentException.class, () -> authenticationService.refreshToken(refreshTokenRequest));
    }

    @Test
    void refreshToken_WhenUserNotFound_ShouldThrowException() {
        // When
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("validRefreshToken");
        when(tokenService.isRefreshToken("validRefreshToken")).thenReturn(true);
        when(tokenService.extractUsernameFromToken("validRefreshToken")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(null);

        // Then & Assert
        assertThrows(IllegalArgumentException.class, () -> authenticationService.refreshToken(refreshTokenRequest));
    }
}
