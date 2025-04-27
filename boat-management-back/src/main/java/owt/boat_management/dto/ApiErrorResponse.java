package owt.boat_management.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp) {
}
