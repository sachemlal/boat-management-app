package owt.boat_management.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import owt.boat_management.dto.LoginRequest;
import owt.boat_management.dto.RefreshTokenRequest;
import owt.boat_management.dto.RegisterRequest;
import owt.boat_management.dto.TokenPair;
import owt.boat_management.model.Role;
import owt.boat_management.model.Token;
import owt.boat_management.model.User;
import owt.boat_management.repository.TokenRepository;
import owt.boat_management.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    /**
     * Register a new user.
     * @param registerRequest
     */
    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        // Check if user with the same username already exist
        if(userRepository.existsByUsername(registerRequest.username())) {
            throw new IllegalArgumentException("Already existing username, please choose another one or try to login");
        }

        // Create new user
        User user = User
                .builder()
                .fullName(registerRequest.fullname())
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role(registerRequest.role() != null ? registerRequest.role() : Role.USER)
                .build();

        userRepository.save(user);
    }

    /**
     * Login a user and generate a token pair for the user.
     * @param loginRequest
     * @return
     */
    @Transactional
    public TokenPair login(LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        // Set authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate Token Pair
        TokenPair tokenPair = tokenService.generateTokenPair(authentication);

        User user = userRepository.findByUsername(loginRequest.username()).orElseThrow();

        revokeAllTokenByUser(user);
        saveUserToken(tokenPair, user);

        return tokenPair;
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findByUserIdAndLoggedOutFalse(user.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> t.setLoggedOut(true));

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(TokenPair tokenPair , User user) {
        Token token = Token.builder()
                .accessToken(tokenPair.accessToken())
                .refreshToken(tokenPair.refreshToken())
                .user(user)
                .build();
        tokenRepository.save(token);
    }

    private void updateUserToken(Token token, String newAccessToken) {
        token.setAccessToken(newAccessToken);
        token.setLoggedOut(false);
        tokenRepository.save(token);
    }

    /**
     * Generate a new access token if the given refresh token is valid.
     * @param request
     * @return
     */
    @Transactional
    public TokenPair refreshToken(@Valid RefreshTokenRequest request) {

        String refreshToken = request.refreshToken();
        // check if the given token is a refresh token
        if(!tokenService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException(String.format("Invalid refresh token %s", refreshToken));
        }

        String username = tokenService.extractUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new IllegalArgumentException("User not found");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        String accessToken = tokenService.generateAccessToken(authentication);
        Token refreshTokenOptional = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token: " + refreshToken + " or token has been revoked"));

        User user = userRepository.findByUsername(username).orElseThrow();

        revokeAllTokenByUser(user);
        updateUserToken(refreshTokenOptional, accessToken);

        return new TokenPair(accessToken, refreshToken);
    }
}
