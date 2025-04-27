package owt.boat_management.dto;

public record BoatRequest
        (
                Long id,
                String name,
                String description
        ) {}
