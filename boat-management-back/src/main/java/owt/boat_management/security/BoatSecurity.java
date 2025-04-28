package owt.boat_management.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import owt.boat_management.repository.BoatRepository;

@Component("boatSecurity")
@RequiredArgsConstructor
public class BoatSecurity {

    private final BoatRepository boatRepository;

    public boolean hasAccessToBoat(Long boatId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return boatRepository.existsByIdAndUserUsername(boatId, username);
    }
}
