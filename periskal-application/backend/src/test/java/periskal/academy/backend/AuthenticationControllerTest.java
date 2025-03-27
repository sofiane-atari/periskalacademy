package periskal.academy.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;
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
            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertNotNull(result.getBody());
            assertEquals(mockToken, result.getBody().getToken());
            assertEquals("SuccesByToken", result.getBody().getLoginResult());
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
        assertNotNull(result);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Failed", result.getBody().getLoginResult());
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
        assertNotNull(result);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertNull(result.getBody());
    }
}
