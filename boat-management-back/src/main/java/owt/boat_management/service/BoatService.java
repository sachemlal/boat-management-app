package owt.boat_management.service;

import owt.boat_management.dto.BoatRequest;
import owt.boat_management.dto.BoatResponse;

import java.util.List;

public interface BoatService {
    List<BoatResponse> getBoatsForCurrentUser();
    BoatResponse getBoatById(Long id);
    BoatResponse addBoatForCurrentUser(BoatRequest boatRequest);
    BoatResponse patchBoat(Long id, BoatRequest boatRequest);
    void deleteBoat(Long id);
}