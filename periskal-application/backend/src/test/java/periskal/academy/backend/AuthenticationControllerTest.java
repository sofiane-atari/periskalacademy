package periskal.academy.backend;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import periskal.academy.backend.controller.AuthenticationController;
import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.model.LoginRequest;
import periskal.academy.backend.service.Authentication.AuthenticationService;
import periskal.academy.backend.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void givenValidCredentials_whenAuthenticate_thenReturnsTokenAndOkStatus() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPass");

        AuthenticateResponse mockResponse = new AuthenticateResponse();
        mockResponse.setUsername("testUser");
        mockResponse.setRole("USER");
        mockResponse.setLoginResult("SuccesByToken");

        String mockToken = "mockJwtToken";

        when(authenticationService.authenticate("testUser", "testPass")).thenReturn(mockResponse);

        try (var jwtUtilMocked = mockStatic(JwtUtil.class)) {
            jwtUtilMocked.when(() -> JwtUtil.generateToken(mockResponse)).thenReturn(mockToken);

            // Act
            ResponseEntity<AuthenticateResponse> result = authenticationController.authenticate(loginRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getToken()).isEqualTo(mockToken);
            assertThat(result.getBody().getLoginResult()).isEqualTo("SuccesByToken");
        }
    }

    @Test
    void givenInvalidCredentials_whenAuthenticate_thenReturnsUnauthorized() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrongUser");
        loginRequest.setPassword("wrongPass");

        AuthenticateResponse mockResponse = new AuthenticateResponse();
        mockResponse.setLoginResult("Failed");

        when(authenticationService.authenticate("wrongUser", "wrongPass")).thenReturn(mockResponse);

        // Act
        ResponseEntity<AuthenticateResponse> result = authenticationController.authenticate(loginRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getLoginResult()).isEqualTo("Failed");
    }

    @Test
    void givenNullResponse_whenAuthenticate_thenReturnsUnauthorized() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPass");

        when(authenticationService.authenticate("testUser", "testPass")).thenReturn(null);

        // Act
        ResponseEntity<AuthenticateResponse> result = authenticationController.authenticate(loginRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isNull();
    }
}
