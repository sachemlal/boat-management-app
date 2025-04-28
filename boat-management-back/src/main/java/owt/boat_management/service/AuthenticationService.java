package owt.boat_management.service;

import jakarta.validation.Valid;
import owt.boat_management.dto.LoginRequest;
import owt.boat_management.dto.RefreshTokenRequest;
import owt.boat_management.dto.RegisterRequest;
import owt.boat_management.dto.TokenPair;


public interface AuthenticationService {

    /**
     * Register a new user.
     * @param registerRequest
     */
    void registerUser(RegisterRequest registerRequest);

    /**
     * Login a user and generate a token pair for the user.
     * @param loginRequest
     * @return
     */
    TokenPair login(LoginRequest loginRequest) ;

    /**
     * Generate a new access token if the given refresh token is valid.
     * @param request
     * @return
     */
    TokenPair refreshToken(@Valid RefreshTokenRequest request);
}
