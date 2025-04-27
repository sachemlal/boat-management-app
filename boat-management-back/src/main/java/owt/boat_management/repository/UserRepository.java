package owt.boat_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import owt.boat_management.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by username.
     * @param username
     * @return
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a user exists by username.
     * @param username
     * @return
     */
    Boolean existsByUsername(String username);

}