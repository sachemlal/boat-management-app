package owt.boat_management.service;

import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import owt.boat_management.dto.BoatRequest;
import owt.boat_management.dto.BoatResponse;
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
    private BoatServiceImpl boatService;

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

        mockSecurityContext();
    }

    private void mockSecurityContext() {
        // 1. Mock Authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("testuser"); // fake username

        // 2. Mock SecurityContext
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        // 3. Set the SecurityContext to the SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAllBoatsByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(boatRepository.findByUserId(user.getId())).thenReturn(List.of(boat));

        List<BoatResponse> boats = boatService.getBoatsForCurrentUser();

        assertNotNull(boats);
        assertEquals(1, boats.size());
        assertEquals("Test Boat", boats.get(0).name());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testGetBoatById_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(boatRepository.findById(boat.getId())).thenReturn(Optional.of(boat));

        BoatResponse foundBoat = boatService.getBoatById(1L);

        assertNotNull(foundBoat);
        assertEquals("Test Boat", foundBoat.name());

        verify(boatRepository).findById(1L);
    }

    @Test
    void testGetBoatById_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            boatService.getBoatById(1L);
        });
    }

    @Test
    void testSaveBoat_Success() {
        when(boatRepository.save(any(Boat.class))).thenReturn(boat);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        BoatResponse savedBoat = boatService.addBoatForCurrentUser(new BoatRequest(null, "Test Boat", "Test Description"));

        assertNotNull(savedBoat);
        assertEquals("Test Boat", savedBoat.name());

        verify(boatRepository).save(any(Boat.class));
    }

    @Test
    void testUpdateBoat_Success() {
        when(boatRepository.findById(boatRequest.id())).thenReturn(Optional.of(boat));
        when(boatRepository.save(any(Boat.class))).thenReturn(boat);

        BoatResponse updatedBoat = boatService.patchBoat(boat.getId(), boatRequest);

        assertNotNull(updatedBoat);
        assertEquals("Updated Boat", updatedBoat.name());
        assertEquals("Updated Description", updatedBoat.description());

        verify(boatRepository).findById(boatRequest.id());
        verify(boatRepository).save(any(Boat.class));
    }

    @Test
    void testUpdateBoat_BoatNotFound() {
        when(boatRepository.findById(boatRequest.id())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            boatService.patchBoat(boatRequest.id(), boatRequest);
        });

        verify(boatRepository).findById(boatRequest.id());
    }

    @Test
    void testDeleteBoat_Success() {
        when(boatRepository.findById(boat.getId())).thenReturn(Optional.of(boat));
        doNothing().when(boatRepository).delete(boat);

        boatService.deleteBoat(boat.getId());

        verify(boatRepository).findById(boat.getId());
        verify(boatRepository).delete(boat);
    }

    @Test
    void testDeleteBoat_BoatNotFound() {
        when(boatRepository.findById(boat.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            boatService.deleteBoat(boat.getId());
        });

        verify(boatRepository).findById(boat.getId());
    }
}
