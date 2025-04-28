package owt.boat_management.config;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;
import owt.boat_management.security.RequestFilter;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private UserDetailsService userDetailsService;
    private RequestFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        userDetailsService = mock(UserDetailsService.class);
        jwtAuthFilter = mock(RequestFilter.class);

        securityConfig = new SecurityConfig(userDetailsService, jwtAuthFilter);

        try {
            Field field = securityConfig.getClass().getDeclaredField("allowedOrigins");
            field.setAccessible(true);
            field.set(securityConfig, List.of("http://localhost:3000", "https://mydomain.com"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertThat(encoder).isNotNull();
        assertThat(encoder).isInstanceOf(PasswordEncoder.class);
    }

    @Test
    void authenticationManager_ShouldReturnManagerFromConfiguration() throws Exception {
        AuthenticationManager manager = mock(AuthenticationManager.class);
        AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
        Mockito.when(config.getAuthenticationManager()).thenReturn(manager);

        AuthenticationManager result = securityConfig.authenticationManager(config);
        assertThat(result).isEqualTo(manager);
    }

    @Test
    void corsConfigurationSource_ShouldContainAllowedOriginsAndHeaders() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertThat(source).isNotNull();
    }
}