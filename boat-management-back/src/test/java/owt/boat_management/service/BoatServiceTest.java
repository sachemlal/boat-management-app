package owt.boat_management.service;

import owt.boat_management.dto.BoatRequest;
import owt.boat_management.model.Boat;
import owt.boat_management.model.User;
import owt.boat_management.repository.BoatRepository;
import owt.boat_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoatServiceTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private BoatRepository boatRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BoatService boatService;

    private User user;
    private Boat boat;
    private BoatRequest boatRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        boat = Boat.builder()
                .id(1L)
                .name("Test Boat")
                .description("Test Description")
                .user(user)
                .build();

        boatRequest = new BoatRequest(1L, "Updated Boat", "Updated Description");
    }

    @Test
    void testGetAllBoatsByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(boatRepository.findBoatByUserId(user.getId())).thenReturn(List.of(boat));

        List<Boat> boats = boatService.getAllBoatsByUsername("testuser");

        assertNotNull(boats);
        assertEquals(1, boats.size());
        assertEquals("Test Boat", boats.get(0).getName());

        verify(userRepository).findByUsername("testuser");
        verify(boatRepository).findBoatByUserId(user.getId());
    }

    @Test
    void testGetBoatById_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(boatRepository.findOneBoatByUserIdAndId(user.getId(), boat.getId())).thenReturn(Optional.of(boat));

        Boat foundBoat = boatService.getBoatById("testuser", 1L);

        assertNotNull(foundBoat);
        assertEquals("Test Boat", foundBoat.getName());

        verify(userRepository).findByUsername("testuser");
        verify(boatRepository).findOneBoatByUserIdAndId(user.getId(), 1L);
    }

    @Test
    void testGetBoatById_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            boatService.getBoatById("testuser", 1L);
        });

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testSaveBoat_Success() {
        when(boatRepository.save(any(Boat.class))).thenReturn(boat);

        Boat savedBoat = boatService.saveBoat(user, new BoatRequest(null, "Test Boat", "Test Description"));

        assertNotNull(savedBoat);
        assertEquals("Test Boat", savedBoat.getName());

        verify(boatRepository).save(any(Boat.class));
    }

    @Test
    void testUpdateBoat_Success() {
        when(boatRepository.findOneBoatByUserIdAndId(user.getId(), boatRequest.id())).thenReturn(Optional.of(boat));
        when(boatRepository.save(any(Boat.class))).thenReturn(boat);

        Boat updatedBoat = boatService.patchBoat(user, boatRequest);

        assertNotNull(updatedBoat);
        assertEquals("Updated Boat", updatedBoat.getName());
        assertEquals("Updated Description", updatedBoat.getDescription());

        verify(boatRepository).findOneBoatByUserIdAndId(user.getId(), boatRequest.id());
        verify(boatRepository).save(any(Boat.class));
    }

    @Test
    void testUpdateBoat_BoatNotFound() {
        when(boatRepository.findOneBoatByUserIdAndId(user.getId(), boatRequest.id())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            boatService.patchBoat(user, boatRequest);
        });

        verify(boatRepository).findOneBoatByUserIdAndId(user.getId(), boatRequest.id());
    }

    @Test
    void testDeleteBoat_Success() {
        when(boatRepository.findOneBoatByUserIdAndId(user.getId(), boat.getId())).thenReturn(Optional.of(boat));
        doNothing().when(boatRepository).delete(boat);

        boatService.deleteBoat(user.getId(), boat.getId());

        verify(boatRepository).findOneBoatByUserIdAndId(user.getId(), boat.getId());
        verify(boatRepository).delete(boat);
    }

    @Test
    void testDeleteBoat_BoatNotFound() {
        when(boatRepository.findOneBoatByUserIdAndId(user.getId(), boat.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            boatService.deleteBoat(user.getId(), boat.getId());
        });

        verify(boatRepository).findOneBoatByUserIdAndId(user.getId(), boat.getId());
    }
}
