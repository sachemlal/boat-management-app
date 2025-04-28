package owt.boat_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import owt.boat_management.model.Boat;

import java.util.List;

public interface BoatRepository extends JpaRepository<Boat, Long> {

    /**
     * Check if a boat exists for this username
     * @param boatId
     * @param username
     * @return
     */
    Boolean existsByIdAndUserUsername(Long boatId, String username);

    /**
     * Find all the boats for a user.
     * @param userId
     * @return
     */
    List<Boat> findByUserId(Long userId);

}