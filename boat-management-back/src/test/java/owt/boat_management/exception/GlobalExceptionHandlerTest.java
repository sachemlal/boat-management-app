package owt.boat_management.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import owt.boat_management.dto.ApiErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {

    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequestResponse() {
        // When
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        when(request.getRequestURI()).thenReturn("/api/test");

        // Then
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleIllegalArgument(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Invalid argument");
        assertThat(response.getBody().status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void handleHttpMessageNotReadableException_ShouldReturnBadRequestResponse() {
        // When
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Message not readable");
        when(request.getRequestURI()).thenReturn("/api/test");

        // Then
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadable(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().message()).isEqualTo("Message not readable");
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequestResponse() {
        // When
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(request.getRequestURI()).thenReturn("/api/test");

        // Then
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleMethodArgumentNotValid(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleAuthenticationCredentialsNotFoundException_ShouldReturnUnauthorizedResponse() {
        // When
        AuthenticationCredentialsNotFoundException exception = new AuthenticationCredentialsNotFoundException("Credentials not found");
        when(request.getRequestURI()).thenReturn("/api/test");

        // Then
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleAuthenticationCredentialsNotFound(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("Credentials not found");
    }

    @Test
    void handleBadCredentialsException_ShouldReturnUnauthorizedResponse() {
        // When
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");
        when(request.getRequestURI()).thenReturn("/api/test");

        // Then
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleBadCredentials(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().message()).isEqualTo("Bad credentials");
    }

    @Test
    void handleGeneralException_ShouldReturnInternalServerErrorResponse() {
        // When
        Exception exception = new Exception("Unexpected error");
        when(request.getRequestURI()).thenReturn("/api/test");

        // Then
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleGeneral(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
    }
}
