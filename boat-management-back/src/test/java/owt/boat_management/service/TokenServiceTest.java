package owt.boat_management.service;

import owt.boat_management.dto.TokenPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    private TokenService tokenService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private final String secretKey = Base64.getEncoder().encodeToString("my-super-secret-key-which-is-very-secure".getBytes());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        tokenService = new TokenService();

        try {
            Field jwtSecretField = tokenService.getClass().getDeclaredField("jwtSecret");
            jwtSecretField.setAccessible(true);
            jwtSecretField.set(tokenService, secretKey);

            Field jwtExpirationMsField = tokenService.getClass().getDeclaredField("jwtExpirationMs");
            jwtExpirationMsField.setAccessible(true);
            jwtExpirationMsField.set(tokenService, 3600000);

            Field refreshExpirationMsField = tokenService.getClass().getDeclaredField("refreshExpirationMs");
            refreshExpirationMsField.setAccessible(true);
            refreshExpirationMsField.set(tokenService, 7200000);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateTokenPair_Success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        TokenPair tokenPair = tokenService.generateTokenPair(authentication);

        assertNotNull(tokenPair);
        assertNotNull(tokenPair.accessToken());
        assertNotNull(tokenPair.refreshToken());

        assertTrue(tokenService.isValidToken(tokenPair.accessToken()));
        assertTrue(tokenService.isValidToken(tokenPair.refreshToken()));
    }

    @Test
    void testGenerateAccessToken_Success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String accessToken = tokenService.generateAccessToken(authentication);

        assertNotNull(accessToken);
        assertTrue(tokenService.isValidToken(accessToken));
    }

    @Test
    void testGenerateRefreshToken_Success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String refreshToken = tokenService.generateRefreshToken(authentication);

        assertNotNull(refreshToken);
        assertTrue(tokenService.isValidToken(refreshToken));
        assertTrue(tokenService.isRefreshToken(refreshToken));
    }

    @Test
    void testValidateTokenForUser_Success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = tokenService.generateAccessToken(authentication);

        when(userDetails.getUsername()).thenReturn("testuser");

        boolean isValid = tokenService.validateTokenForUser(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testValidateTokenForUser_InvalidUser() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = tokenService.generateAccessToken(authentication);

        when(userDetails.getUsername()).thenReturn("otheruser");

        boolean isValid = tokenService.validateTokenForUser(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void testExtractUsernameFromToken_Success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = tokenService.generateAccessToken(authentication);

        String username = tokenService.extractUsernameFromToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void testIsRefreshToken_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String refreshToken = tokenService.generateRefreshToken(authentication);

        assertTrue(tokenService.isRefreshToken(refreshToken));
    }

    @Test
    void testIsRefreshToken_ReturnsFalse() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String accessToken = tokenService.generateAccessToken(authentication);

        assertFalse(tokenService.isRefreshToken(accessToken));
    }

    @Test
    void testIsValidToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(RuntimeException.class, () -> tokenService.isValidToken(invalidToken));
    }

    @Test
    void testExtractUsernameFromToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(RuntimeException.class, () -> tokenService.extractUsernameFromToken(invalidToken));
    }
}
