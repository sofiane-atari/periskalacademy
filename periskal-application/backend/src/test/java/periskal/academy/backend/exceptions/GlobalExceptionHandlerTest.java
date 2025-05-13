package periskal.academy.backend.exceptions;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import periskal.academy.backend.security.exceptions.GlobalExceptionHandler;
import periskal.academy.backend.security.exceptions.authentication.*;
import periskal.academy.backend.security.exceptions.general.*;
import periskal.academy.backend.dto.ErrorResponse;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    // #region Authentication Exceptions

    @Test
    void givenAuthenticationCommunicationException_whenHandleCommunicationException_thenReturnsServiceUnavailable() {
        // Given
        AuthenticationCommunicationException ex = new AuthenticationCommunicationException("Communication error",
                new Exception());

        // When
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleCommunicationException(ex,
                webRequest);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(responseEntity.getBody().getMessage())
                .isEqualTo("Authentication service is currently unavailable. Please try again later.");
        assertThat(responseEntity.getBody().getDetails()).contains("Communication error");
    }

    @Test
    void givenAuthenticationServerErrorException_whenHandleServerErrorException_thenReturnsBadGateway() {
        // Given
        AuthenticationServerErrorException ex = new AuthenticationServerErrorException("Server error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server failed");

        // When
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleServerErrorException(ex,
                webRequest);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(responseEntity.getBody().getMessage())
                .isEqualTo("Authentication service reported an internal error.");
        assertThat(responseEntity.getBody().getDetails()).contains("External service failed with status 500");
    }

    @Test
    void givenAuthenticationParsingException_whenHandleParsingException_thenReturnsInternalServerError() {
        // Given
        AuthenticationParsingException ex = new AuthenticationParsingException("Parsing failed", new Exception(),
                "Failed to parse XML");

        // When
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleParsingException(ex, webRequest);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getBody().getMessage())
                .isEqualTo("Failed to process the response from the authentication service.");
        assertThat(responseEntity.getBody().getDetails()).contains("Parsing failed");
    }

    @Test
    void givenAuthenticationInvalidCredentialsException_whenHandleInvalidCredentials_thenReturnsUnauthorized() {
        // Given
        AuthenticationInvalidCredentialsException ex = new AuthenticationInvalidCredentialsException(
                "Invalid credentials");

        // When
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleInvalidCredentials(ex, webRequest);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody().getMessage())
                .isEqualTo("Authentication failed. Please check your username and password.");
        assertThat(responseEntity.getBody().getDetails()).isEqualTo("Invalid credentials provided.");
    }

    @Test
    void givenAuthenticationServiceException_whenHandleBaseAuthException_thenReturnsInternalServerError() {
        // Given
        AuthenticationServiceException ex = new AuthenticationServiceException("Unexpected error", new Exception());

        // When
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleBaseAuthException(ex, webRequest);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getBody().getMessage())
                .isEqualTo("An unexpected error occurred during authentication.");
        assertThat(responseEntity.getBody().getDetails()).contains("Unexpected error");
    }

    // #endregion

    // #region General Exceptions

    @Test
    void givenResourceNotFoundException_whenHandleEntityNotFoundException_thenReturnsNotFound() {
        // Given
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        // When
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleEntityNotFoundException(ex,
                webRequest);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("The requested resource was not found.");
        assertThat(responseEntity.getBody().getDetails()).isEqualTo("Resource not found");
    }

    @Test
    void givenValidationException_whenHandleValidationException_thenReturnsBadRequest() {
        // Given
        ValidationException ex = new ValidationException("Validation failed");

        // When
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleValidationException(ex, webRequest);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Validation failed");
    }

    // #endregion
}
