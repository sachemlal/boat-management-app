package owt.boat_management.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import owt.boat_management.dto.LoginRequest;
import owt.boat_management.dto.RefreshTokenRequest;
import owt.boat_management.dto.RegisterRequest;
import owt.boat_management.dto.TokenPair;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import owt.boat_management.service.AuthenticationService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    /**
     * Register a new user
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok(Map.of("message", "User created successfully"));
    }

    /**
     * Login a user and generate a token pair for the user.
     * @param loginRequest
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenPair tokenPair = authService.login(loginRequest);
        return ResponseEntity.ok(tokenPair);
    }

    /**
     * Refresh the access token using the refresh token.
     * @param request
     * @return
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenPair tokenPair = authService.refreshToken(request);
        return ResponseEntity.ok(tokenPair);
    }

}
