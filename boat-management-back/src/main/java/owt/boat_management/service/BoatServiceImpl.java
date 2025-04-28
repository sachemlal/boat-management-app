package owt.boat_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import owt.boat_management.dto.BoatRequest;
import owt.boat_management.dto.BoatResponse;
import owt.boat_management.model.Boat;
import owt.boat_management.model.User;
import owt.boat_management.repository.BoatRepository;
import owt.boat_management.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoatServiceImpl implements BoatService {

    private final BoatRepository boatRepository;
    private final UserRepository userRepository; // if needed

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Get all the boats
    public List<BoatResponse> getBoatsForCurrentUser() {
        User user = getCurrentUser();
        List<Boat> boats = boatRepository.findByUserId(user.getId());

        return boats.stream().map(boat -> new BoatResponse(boat.getId(), boat.getName(), boat.getDescription())).toList();
    }

    // Get a boat by id
    public BoatResponse getBoatById(Long boatId) {
        Boat boat = boatRepository.findById(boatId).orElseThrow(() -> new IllegalArgumentException("Boat not found. Please check the id and try again."));

        return new BoatResponse(boat.getId(), boat.getName(), boat.getDescription());
    }

    /**
     * Save a new boat.
     * @param boatRequest
     * @return
     */
    public BoatResponse addBoatForCurrentUser(BoatRequest boatRequest) {

        User user = getCurrentUser();
        Boat boatEntity = Boat.builder()
                .name(boatRequest.name())
                .description(boatRequest.description())
                .user(user)
                .build();

        Boat boat = boatRepository.save(boatEntity);
        return new BoatResponse(boat.getId(), boat.getName(), boat.getDescription());
    }


    /**
     * Delete a boat.
     * @param boatId
     */
    public void deleteBoat(Long boatId) {
        Boat boatEntity = boatRepository.findById(boatId).orElseThrow(() -> new IllegalArgumentException("Boat not found"));
        boatRepository.delete(boatEntity);
    }

    @Override
    public BoatResponse patchBoat(Long boatId, BoatRequest boatRequest) {
        // Fetch the boat
        Boat existingBoat = boatRepository.findById(boatId)
                .orElseThrow(() -> new IllegalArgumentException("Boat not found with id: " + boatId));

        // Update fields if they are not null
        existingBoat.setName(boatRequest.name() == null ? existingBoat.getName() : boatRequest.name());
        existingBoat.setDescription(boatRequest.description() == null ? existingBoat.getDescription() : boatRequest.description());

        // Save updated boat
        Boat editedBoat = boatRepository.save(existingBoat);
        return new BoatResponse(editedBoat.getId(), editedBoat.getName(), editedBoat.getDescription());
    }

}

