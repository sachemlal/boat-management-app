package owt.boat_management.dto;

public record TokenPair
        (
                String accessToken,
                String refreshToken
        ) {}

