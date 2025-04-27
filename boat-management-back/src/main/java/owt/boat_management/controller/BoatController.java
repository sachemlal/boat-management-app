package owt.boat_management.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import owt.boat_management.dto.BoatRequest;
import owt.boat_management.dto.BoatResponse;
import owt.boat_management.model.Boat;
import owt.boat_management.model.User;
import owt.boat_management.service.BoatService;
import owt.boat_management.service.UserDetailsServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/boat")
@RequiredArgsConstructor
public class BoatController {

    private final BoatService boatService;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Get all the boats for a user.
     * @param userDetails
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getAllBoats(@AuthenticationPrincipal UserDetails userDetails) {
        List<Boat> boats = boatService.getAllBoatsByUsername(userDetails.getUsername());
        List<BoatResponse> boatResponses = boats.stream()
                .map(boat -> new BoatResponse(boat.getId(), boat.getName(), boat.getDescription())).toList();
        return ResponseEntity.ok(boatResponses);
    }

    /**
     * Get a boat by id.
     * @param userDetails
     * @param boatId
     * @return
     */
    @GetMapping("/{boatId}")
    public ResponseEntity<?> getBoat(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String boatId) {
        Boat boat = boatService.getBoatById(userDetails.getUsername(), Long.parseLong(boatId));
        return ResponseEntity.ok(new BoatResponse(boat.getId(), boat.getName(), boat.getDescription()));
    }

    /**
     * Add a new boat.
     * @param userDetails
     * @param boatRequest
     * @return
     */
    @PostMapping
    public ResponseEntity<?> addBoat(@AuthenticationPrincipal UserDetails userDetails,@Valid @RequestBody BoatRequest boatRequest) {

        User user = userDetailsService.loadUserByUserDetails(userDetails.getUsername());
        Boat boat = boatService.saveBoat(user, boatRequest);

        return ResponseEntity.ok(new BoatResponse(boat.getId(), boat.getName(), boat.getDescription()));
    }

    /**
     * Update a boat.
     * @param userDetails
     * @param boatId
     * @param boatRequest
     * @return
     */
    @PatchMapping("/{boatId}")
    public ResponseEntity<?> updateBoat(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String boatId, @Valid @RequestBody BoatRequest boatRequest) {

        BoatRequest boatRequestUpdated = new BoatRequest(Long.parseLong(boatId), boatRequest.name(), boatRequest.description());
        User user = userDetailsService.loadUserByUserDetails(userDetails.getUsername());
        Boat boat = boatService.patchBoat(user, boatRequestUpdated);

        return ResponseEntity.ok(new BoatResponse(boat.getId(), boat.getName(), boat.getDescription()));
    }

    /**
     * Delete a boat.
     * @param userDetails
     * @param boatId
     * @return
     */
    @DeleteMapping("/{boatId}")
    public ResponseEntity<?> deleteBoat(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String boatId) {

        assert userDetails != null : "User not found";
        User user = userDetailsService.loadUserByUserDetails(userDetails.getUsername());
        boatService.deleteBoat(user.getId(), Long.parseLong(boatId));

        return ResponseEntity.noContent().build();
    }

}