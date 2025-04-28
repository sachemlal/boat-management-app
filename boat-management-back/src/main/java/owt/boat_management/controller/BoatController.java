package owt.boat_management.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import owt.boat_management.dto.BoatRequest;
import owt.boat_management.dto.BoatResponse;
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
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getAllBoats() {

        List<BoatResponse> response = boatService.getBoatsForCurrentUser();
        return ResponseEntity.ok(response);
    }

    /**
     * Get a boat by id
     * @param boatId
     * @return
     */
    @GetMapping("/{boatId}")
    @PreAuthorize("@boatSecurity.hasAccessToBoat(#boatId)")
    public ResponseEntity<?> getBoat(@PathVariable String boatId) {

        BoatResponse response  = boatService.getBoatById(Long.parseLong(boatId));
        return ResponseEntity.ok(response);
    }

    /**
     * Add a new boat.
     * @param boatRequest
     * @return
     */
    @PostMapping
    public ResponseEntity<?> addBoat(@Valid @RequestBody BoatRequest boatRequest) {

        BoatResponse response = boatService.addBoatForCurrentUser(boatRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a boat.
     * @param boatId
     * @param boatRequest
     * @return
     */
    @PatchMapping("/{boatId}")
    @PreAuthorize("@boatSecurity.hasAccessToBoat(#boatId)")
    public ResponseEntity<?> patchBoat(@PathVariable String boatId, @Valid @RequestBody BoatRequest boatRequest) {

        BoatRequest boatRequestUpdated = new BoatRequest(Long.parseLong(boatId), boatRequest.name(), boatRequest.description());
        BoatResponse response = boatService.patchBoat(Long.parseLong(boatId), boatRequestUpdated);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a boat.
     * @param boatId
     * @return
     */
    @DeleteMapping("/{boatId}")
    @PreAuthorize("@boatSecurity.hasAccessToBoat(#boatId)")
    public ResponseEntity<?> deleteBoat(@PathVariable String boatId) {

        boatService.deleteBoat(Long.parseLong(boatId));
        return ResponseEntity.noContent().build();
    }

}