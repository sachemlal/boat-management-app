package owt.boat_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import owt.boat_management.model.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    /**
     * Find all the tokens for a user that are not logged out.
     * @param userId
     * @return
     */
    List<Token> findByUserIdAndLoggedOutFalse(Long userId);

    /**
     * Find a token by refresh token.
     * @param token
     * @return
     */
    Optional<Token> findByRefreshToken(String token);
}
