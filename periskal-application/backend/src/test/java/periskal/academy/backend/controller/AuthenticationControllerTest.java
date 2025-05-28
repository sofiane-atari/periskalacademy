package periskal.academy.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;

import periskal.academy.backend.dto.ErrorResponse;
import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.model.LoginRequest;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationCommunicationException;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationInvalidCredentialsException;
import periskal.academy.backend.service.authentication.AuthenticationService;
import periskal.academy.backend.service.authentication.DongleAuthenticationService;
import periskal.academy.backend.util.JwtUtil;

@WebMvcTest(controllers = AuthenticationController.class, excludeAutoConfiguration = {
                SecurityAutoConfiguration.class })
class AuthenticationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private AuthenticationService authenticationService;

        @MockitoBean
        private DongleAuthenticationService dongleAuthenticationService; 

        private LoginRequest loginRequest;

        @BeforeEach
        void setUp() {
                loginRequest = new LoginRequest();
                loginRequest.setUsername("testUser");
                loginRequest.setPassword("testPass");
        }

        @Test
        void givenValidCredentials_whenAuthenticate_thenReturnsTokenAndOkStatus() throws Exception {

                // Arrange
                AuthenticateResponse mockResponse = new AuthenticateResponse();
                mockResponse.setUsername("testUser");
                mockResponse.setRole("USER");
                mockResponse.setLoginResult("SuccesByToken");

                String mockToken = "mockJwtToken";

                when(authenticationService.authenticate("testUser", "testPass")).thenReturn(mockResponse);

                try (var jwtUtilMocked = mockStatic(JwtUtil.class)) {
                        jwtUtilMocked.when(() -> JwtUtil.generateToken(mockResponse)).thenReturn(mockToken);

                        // Act
                        MvcResult mvcResult = mockMvc
                                        .perform(post("/api/auth/authenticate").contentType(MediaType.APPLICATION_JSON)
                                                        .accept(MediaType.APPLICATION_JSON)
                                                        .content(objectMapper.writeValueAsString(loginRequest)))
                                        .andReturn();

                        // Assert
                        MockHttpServletResponse response = mvcResult.getResponse();
                        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

                        AuthenticateResponse actualResponse = objectMapper.readValue(response.getContentAsString(),
                                        AuthenticateResponse.class);

                        assertThat(actualResponse).isNotNull();
                        assertThat(actualResponse.getUsername()).isEqualTo("testUser");
                        assertThat(actualResponse.getToken()).isEqualTo(mockToken);
                        assertThat(actualResponse.getLoginResult()).isEqualTo("SuccesByToken");

                }
        }

        @Test
        void givenInvalidCredentials_whenAuthenticate_thenReturnsUnauthorized() throws Exception {

                // Arrange
                String username = "wrongUser";
                String password = "wrongPass";
                loginRequest.setUsername(username);
                loginRequest.setPassword(password);

                String exceptionMessage = "Authentication failed: Invalid username or password.";

                when(authenticationService.authenticate(username, password))
                                .thenThrow(new AuthenticationInvalidCredentialsException(exceptionMessage));

                // Act
                MvcResult mvcResult = mockMvc
                                .perform(post("/api/auth/authenticate").contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andReturn();

                // Assert
                MockHttpServletResponse response = mvcResult.getResponse();
                assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

                ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(),
                                ErrorResponse.class);

                assertThat(errorResponse).isNotNull();
                assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                assertThat(errorResponse.getError()).isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase());
                assertThat(errorResponse.getMessage())
                                .isEqualTo("Authentication failed. Please check your username and password.");
                assertThat(errorResponse.getDetails()).isEqualTo("Invalid credentials provided.");
                assertThat(errorResponse.getValidationErrors()).isNull();
                assertThat(errorResponse.getTimestamp()).isNotNull();

        }

        @Test
        void givenCommunicationError_whenAuthenticate_thenReturnsServiceUnavailable() throws Exception {

                // Arrange
                String username = "testUser";
                String password = "testPass";
                String exceptionMessage = "SOAP authentication request failed: Communication error";

                when(authenticationService.authenticate(username, password)).thenThrow(
                                new AuthenticationCommunicationException(exceptionMessage,
                                                new RuntimeException("Network timeout")));

                // Act
                MvcResult mvcResult = mockMvc
                                .perform(post("/api/auth/authenticate").contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andReturn();

                // Assert
                MockHttpServletResponse response = mvcResult.getResponse();
                assertThat(response.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());

                ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(),
                                ErrorResponse.class);

                assertThat(errorResponse).isNotNull();
                assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
                assertThat(errorResponse.getError()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
                assertThat(errorResponse.getMessage())
                                .isEqualTo("Authentication service is currently unavailable. Please try again later.");
                assertThat(errorResponse.getDetails()).isEqualTo(exceptionMessage); // Details contain original message
                assertThat(errorResponse.getValidationErrors()).isNull();
                assertThat(errorResponse.getTimestamp()).isNotNull();
        }

        @Test
        void givenValidDongleToken_whenDecodeToken_thenReturnsTokenAndOkStatus() throws Exception {

                // Arrange
                String dongleToken = "validDongleToken123";
                
                AuthenticateResponse mockResponse = new AuthenticateResponse();
                mockResponse.setUsername("dongleUser");
                mockResponse.setRole("DONGLE_USER");
                mockResponse.setLoginResult("SuccesByToken");

                String mockJwtToken = "mockJwtTokenForDongle";

                when(dongleAuthenticationService.authenticateDongle(dongleToken)).thenReturn(mockResponse);

                try (var jwtUtilMocked = mockStatic(JwtUtil.class)) {
                        jwtUtilMocked.when(() -> JwtUtil.generateToken(mockResponse)).thenReturn(mockJwtToken);

                        // Act
                        MvcResult mvcResult = mockMvc
                                .perform(get("/api/auth/decode-token")
                                        .param("login", dongleToken)
                                        .accept(MediaType.APPLICATION_JSON))
                                .andReturn();

                        // Assert
                        MockHttpServletResponse response = mvcResult.getResponse();
                        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

                        AuthenticateResponse actualResponse = objectMapper.readValue(response.getContentAsString(),
                                AuthenticateResponse.class);

                        assertThat(actualResponse).isNotNull();
                        assertThat(actualResponse.getUsername()).isEqualTo("dongleUser");
                        assertThat(actualResponse.getToken()).isEqualTo(mockJwtToken);
                        assertThat(actualResponse.getLoginResult()).isEqualTo("SuccesByToken");
                }
        }

}
