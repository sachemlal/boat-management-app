package owt.boat_management.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import owt.boat_management.model.Role;


public record RegisterRequest
        (
                @NotBlank(message = "Full name is required")
                @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
                String fullname,

                @NotBlank(message = "Username is required")
                @Size(min = 3, max = 10, message = "Username must be between 3 and 10 characters")
                String username,

                @NotBlank(message = "Password is required")
                @Size(min = 3, message = "Password must be at least 3 characters")
                String password,

                Role role
        ) {}