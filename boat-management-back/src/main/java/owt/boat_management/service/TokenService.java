package owt.boat_management.service;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import owt.boat_management.dto.TokenPair;


public interface TokenService {

    /**
     * Generate a token pair.
     * @param authentication
     * @return
     */
    TokenPair generateTokenPair(Authentication authentication);

    /**
     * Generate an access token.
     * @param authentication
     * @return
     */
    String generateAccessToken(Authentication authentication);

    /**
     * Generate a refresh token.
     * @param authentication
     * @return
     */
    String generateRefreshToken(Authentication authentication);



    /**
     * Validate a token for a user.
     * @param token
     * @param userDetails
     * @return
     */
    boolean validateTokenForUser(String token, UserDetails userDetails);

    /**
     * Check if a toke is valid.
     * @param token
     * @return
     */
    boolean isValidToken(String token);

    /**
     * Extract the username from a token.
     * @param token
     * @return
     */
    String extractUsernameFromToken(String token);

    /**
     * Check if the token is a refresh token.
     * @param token
     * @return
     */
    boolean isRefreshToken(String token);
}
