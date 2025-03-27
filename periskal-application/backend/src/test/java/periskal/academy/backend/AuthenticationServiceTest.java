package periskal.academy.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.service.Authentication.AuthenticationService;
import periskal.academy.backend.util.EncryptionUtil;

public class AuthenticationServiceTest {

    private AuthenticationService authenticationService;
    private MockRestServiceServer mockServer;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws Exception {
        authenticationService = new AuthenticationService();
        
        // Use reflection to access the protected RestTemplate instance from parent class
        Field restTemplateField = AuthenticationService.class.getSuperclass().getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplate = (RestTemplate) restTemplateField.get(authenticationService);
        
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    private String getAuthenticateEndpointUrl() throws Exception {
        Field endpointUrlField = AuthenticationService.class.getSuperclass().getDeclaredField("authenticateEndpointUrl");
        endpointUrlField.setAccessible(true);
        return (String) endpointUrlField.get(authenticationService);
    }

    @Test
    void authenticate_SuccessfulResponse_ReturnsParsedAuthResponse() throws Exception {
        String sampleResponseXml = """
                                   <?xml version="1.0" encoding="utf-8"?>
                                   <soap12:Envelope xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
                                     <soap12:Body>
                                       <AuthenticateResponse>
                                         <AuthenticateResult>
                                           <ID>12345</ID>
                                           <Username>testUser</Username>
                                           <Firstname>John</Firstname>
                                           <Lastname>Doe</Lastname>
                                           <Shipname>TestShip</Shipname>
                                           <Role>Admin</Role>
                                           <LoginResult>Success</LoginResult>
                                           <Products>
                                             <string>ProductA</string>
                                             <string>ProductB</string>
                                           </Products>
                                         </AuthenticateResult>
                                       </AuthenticateResponse>
                                     </soap12:Body>
                                   </soap12:Envelope>""";

        // Mock the EncryptionUtil.encode method
        try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
            mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                    .thenReturn("encoded_token");

            mockServer.expect(requestTo(getAuthenticateEndpointUrl()))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(header("Content-Type", "application/soap+xml;charset=utf-8"))
                    .andRespond(withSuccess(sampleResponseXml, MediaType.TEXT_XML));

            AuthenticateResponse response = authenticationService.authenticate("user", "pass");

            assertNotNull(response);
            assertEquals("12345", response.getId());
            assertEquals("testUser", response.getUsername());
            assertEquals("John", response.getFirstname());
            assertEquals("Doe", response.getLastname());
            assertEquals("TestShip", response.getShipname());
            assertEquals("Admin", response.getRole());
            assertEquals("Success", response.getLoginResult());
            assertIterableEquals(List.of("ProductA", "ProductB"), response.getProducts());

            mockServer.verify();
        }
    }

    @Test
    void authenticate_HttpError_ReturnsNull() throws Exception {
        try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
            mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                    .thenReturn("encoded_token");

            mockServer.expect(requestTo(getAuthenticateEndpointUrl()))
                    .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

            AuthenticateResponse response = authenticationService.authenticate("user", "pass");

            assertNull(response);
            mockServer.verify();
        }
    }

    @Test
    void authenticate_InvalidXmlResponse_ReturnsPartiallyParsedResponse() throws Exception {
        String invalidXml = "<InvalidXml></InvalidXml>";

        try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
            mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                    .thenReturn("encoded_token");

            mockServer.expect(requestTo(getAuthenticateEndpointUrl()))
                    .andRespond(withSuccess(invalidXml, MediaType.TEXT_XML));

            AuthenticateResponse response = authenticationService.authenticate("user", "pass");

            assertNotNull(response);
            assertNull(response.getId());
            assertNull(response.getUsername());
            mockServer.verify();
        }
    }

    @Test
    void authenticate_RestTemplateThrowsException_ReturnsNull() throws Exception {
        try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
            mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                    .thenReturn("encoded_token");

            mockServer.expect(requestTo(getAuthenticateEndpointUrl()))
                    .andRespond(withBadRequest());

            AuthenticateResponse response = authenticationService.authenticate("user", "invalid");

            assertNull(response);
            mockServer.verify();
        }
    }

    @Test
    void authenticate_EncryptionUtilThrowsException_ReturnsNull() throws Exception {
        try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
            mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                    .thenThrow(new RuntimeException("Encryption failed"));

            AuthenticateResponse response = authenticationService.authenticate("user", "pass");

            assertNull(response);
        }
    }
}