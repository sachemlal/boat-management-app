package owt.boat_management.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import owt.boat_management.dto.BoatRequest;
import owt.boat_management.dto.BoatResponse;
import owt.boat_management.model.Boat;
import owt.boat_management.model.Role;
import owt.boat_management.model.User;
import owt.boat_management.service.BoatService;
import owt.boat_management.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class BoatControllerTest {

    @Mock
    private BoatService boatService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private BoatController boatController;

    private UserDetails userDetails;
    private BoatRequest boatRequest;
    private Boat boat;

    @BeforeEach
    void setUp() {
        // Mock user details and boat request
        userDetails = mock(UserDetails.class);
        boatRequest = new BoatRequest(1L, "Boat Name", "Boat Description");

        // Mock the Boat object
        boat = new Boat(1L, "Boat Name", "Boat Description", new User());
    }

    @Test
    void getAllBoats_ShouldReturnListOfBoats() {
        // Arrange: Mock the service to return a list of boats
        when(boatService.getAllBoatsByUsername(userDetails.getUsername()))
                .thenReturn(List.of(boat));

        // Act
        ResponseEntity<?> response = boatController.getAllBoats(userDetails);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        List<BoatResponse> boatResponses = (List<BoatResponse>) response.getBody();
        assertThat(boatResponses).hasSize(1);
        assertThat(boatResponses.get(0).id()).isEqualTo(boat.getId());
        assertThat(boatResponses.get(0).name()).isEqualTo(boat.getName());

        // Verify
        verify(boatService, times(1)).getAllBoatsByUsername(userDetails.getUsername());
    }

    @Test
    void getBoat_ShouldReturnBoat() {
        // When
        when(boatService.getBoatById(userDetails.getUsername(), boat.getId()))
                .thenReturn(boat);

        // Then
        ResponseEntity<?> response = boatController.getBoat(userDetails, String.valueOf(boat.getId()));

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        BoatResponse boatResponse = (BoatResponse) response.getBody();
        assertThat(boatResponse.id()).isEqualTo(boat.getId());
        assertThat(boatResponse.name()).isEqualTo(boat.getName());

        // Verify
        verify(boatService, times(1)).getBoatById(userDetails.getUsername(), boat.getId());
    }

    @Test
    void addBoat_ShouldReturnCreatedBoat() {
        // When
        when(userDetailsService.loadUserByUserDetails(userDetails.getUsername()))
                .thenReturn(new User(1L,"my name", "username", "password", Role.USER));
        when(boatService.saveBoat(any(User.class), eq(boatRequest)))
                .thenReturn(boat);

        // Then
        ResponseEntity<?> response = boatController.addBoat(userDetails, boatRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        BoatResponse boatResponse = (BoatResponse) response.getBody();
        assertThat(boatResponse.id()).isEqualTo(boat.getId());
        assertThat(boatResponse.name()).isEqualTo(boat.getName());

        // Verify
        verify(boatService, times(1)).saveBoat(any(User.class), eq(boatRequest));
    }

    @Test
    void updateBoat_ShouldReturnUpdatedBoat() {
        // When
        Boat updatedBoat = new Boat(boat.getId(), "Updated Boat", "Updated Description", new User());
        BoatRequest updatedBoatRequest = new BoatRequest(boat.getId(), "Updated Boat", "Updated Description");
        when(userDetailsService.loadUserByUserDetails(userDetails.getUsername()))
                .thenReturn(new User(1L, "my name", "username", "password", Role.USER));
        when(boatService.patchBoat(any(User.class), eq(updatedBoatRequest)))
                .thenReturn(updatedBoat);

        // Then
        ResponseEntity<?> response = boatController.updateBoat(userDetails, String.valueOf(boat.getId()), updatedBoatRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        BoatResponse boatResponse = (BoatResponse) response.getBody();
        assertThat(boatResponse.id()).isEqualTo(boat.getId());
        assertThat(boatResponse.name()).isEqualTo("Updated Boat");
        assertThat(boatResponse.description()).isEqualTo("Updated Description");

        // Verify that the service method was called
        verify(boatService, times(1)).patchBoat(any(User.class), eq(updatedBoatRequest));
    }

    @Test
    void deleteBoat_ShouldReturnNoContent() {
        // When
        when(userDetailsService.loadUserByUserDetails(userDetails.getUsername()))
                .thenReturn(new User(1L, "my name", "username", "password", Role.USER));

        // Then
        ResponseEntity<?> response = boatController.deleteBoat(userDetails, String.valueOf(boat.getId()));

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(204);

        // Verify
        verify(boatService, times(1)).deleteBoat(1L, boat.getId());
    }
}
