package owt.boat_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import owt.boat_management.dto.BoatRequest;
import owt.boat_management.model.Boat;
import owt.boat_management.model.User;
import owt.boat_management.repository.BoatRepository;
import owt.boat_management.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoatService {

    private final UserDetailsServiceImpl userDetailsService;
    private final BoatRepository boatRepository;
    private final UserRepository userRepository;

    // Get all the boats
    public List<Boat> getAllBoatsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return boatRepository.findBoatByUserId(user.getId());
    }

    // Get a boat by id
    public Boat getBoatById(String username, Long boatId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return boatRepository.findOneBoatByUserIdAndId(user.getId(), boatId).orElseThrow(() -> new IllegalArgumentException("Boat not found. Please check the id and try again."));
    }

    /**
     * Save a new boat.
     * @param user
     * @param boatRequest
     * @return
     */
    @Transactional
    public Boat saveBoat(User user, BoatRequest boatRequest) {

        Boat boatEntity = Boat.builder()
                .name(boatRequest.name())
                .description(boatRequest.description())
                .user(user)
                .build();

        return boatRepository.save(boatEntity);
    }

    /**
     * Update a boat.
     * @param user
     * @param boatRequest
     * @return
     */
    @Transactional
    public Boat patchBoat(User user, BoatRequest boatRequest) {

        Boat boatEntity = boatRepository.findOneBoatByUserIdAndId(user.getId(), boatRequest.id()).orElseThrow(() -> new IllegalArgumentException("Boat not found"));

        boatEntity.setName(boatRequest.name() == null ? boatEntity.getName() : boatRequest.name());
        boatEntity.setDescription(boatRequest.description() == null ? boatEntity.getDescription() : boatRequest.description());
        boatEntity.setUser(user);

        return boatRepository.save(boatEntity);
    }

    /**
     * Delete a boat.
     * @param userId
     * @param boatId
     */
    @Transactional
    public void deleteBoat(Long userId, Long boatId) {
        Boat boatEntity = boatRepository.findOneBoatByUserIdAndId(userId, boatId).orElseThrow(() -> new IllegalArgumentException("Boat not found"));
        boatRepository.delete(boatEntity);
    }
}
