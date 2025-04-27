package owt.boat_management.service;

import owt.boat_management.model.Role;
import owt.boat_management.model.User;
import owt.boat_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .role(Role.USER)  // Assuming you have a Role enum with USER, ADMIN, etc.
                .build();
    }

    @Test
    void testLoadUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER")));

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknownuser");
        });

        verify(userRepository).findByUsername("unknownuser");
    }

    @Test
    void testLoadUserByUserDetails_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User loadedUser = userDetailsService.loadUserByUserDetails("testuser");

        assertNotNull(loadedUser);
        assertEquals("testuser", loadedUser.getUsername());
        assertEquals("password123", loadedUser.getPassword());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUserDetails_UserNotFound() {
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUserDetails("unknownuser");
        });

        verify(userRepository).findByUsername("unknownuser");
    }
}
