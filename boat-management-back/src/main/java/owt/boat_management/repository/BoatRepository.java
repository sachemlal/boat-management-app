package owt.boat_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import owt.boat_management.model.Boat;

import java.util.List;
import java.util.Optional;

public interface BoatRepository extends JpaRepository<Boat, Long> {

    /**
     * Find all the boats for a user.
     * @param userId
     * @return
     */
    List<Boat> findBoatByUserId(Long userId);

    /**
     * Find a boat by id and user id.
     * @param userId
     * @param boatId
     * @return
     */
    Optional<Boat> findOneBoatByUserIdAndId(Long userId, Long boatId);
}