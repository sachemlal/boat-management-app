package owt.boat_management.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import owt.boat_management.service.TokenService;
import owt.boat_management.service.UserDetailsServiceImpl;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RequestFilterTest {

    @InjectMocks
    private RequestFilter requestFilter;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilterInternal_WhenAuthorizationHeaderIsAbsent_ShouldCallFilterChainWithoutAuthentication() throws ServletException, IOException {
        // When
        when(request.getHeader("Authorization")).thenReturn(null);

        // Then
        requestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WhenTokenIsInvalid_ShouldCallFilterChainWithoutAuthentication() throws ServletException, IOException {
        // When
        String authHeader = "Bearer invalid-token";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.isValidToken("invalid-token")).thenReturn(false);

        // Then
        requestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WhenTokenIsValid_ShouldAuthenticateUser() throws ServletException, IOException {
        // When
        String authHeader = "Bearer valid-token";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.isValidToken("valid-token")).thenReturn(true);
        when(tokenService.extractUsernameFromToken("valid-token")).thenReturn("user");
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(tokenService.validateTokenForUser("valid-token", userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        // Then
        requestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
    }

    @Test
    void doFilterInternal_WhenAuthorizationHeaderDoesNotStartWithBearer_ShouldCallFilterChainWithoutAuthentication() throws ServletException, IOException {
        // When
        String authHeader = "Basic some-token";
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        // Then
        requestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldNotFilter_ShouldReturnTrueForAuthUrls() {
        // When
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        // Then
        boolean result = requestFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldNotFilter_ShouldReturnFalseForNonAuthUrls() {
        // When
        when(request.getRequestURI()).thenReturn("/api/boat/list");

        // Then
        boolean result = requestFilter.shouldNotFilter(request);

        // Assert
        assertFalse(result);
    }
}
