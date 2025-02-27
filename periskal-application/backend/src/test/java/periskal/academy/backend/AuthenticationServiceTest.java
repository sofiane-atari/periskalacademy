package periskal.academy.backend;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.web.client.RestTemplate;

import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.service.AuthenticationService;

public class AuthenticationServiceTest {

    private AuthenticationService authenticationService;
    private MockRestServiceServer mockServer;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws Exception {
        authenticationService = new AuthenticationService();
        
        // Use reflection to access the private RestTemplate instance
        Field restTemplateField = AuthenticationService.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplate = (RestTemplate) restTemplateField.get(authenticationService);
        
        mockServer = MockRestServiceServer.createServer(restTemplate);
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

        mockServer.expect(requestTo(authenticationService.authenticateEndpointUrl))
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

    @Test
    void authenticate_HttpError_ReturnsNull() throws Exception {
        mockServer.expect(requestTo(authenticationService.authenticateEndpointUrl))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        AuthenticateResponse response = authenticationService.authenticate("user", "pass");

        assertNull(response);
        mockServer.verify();
    }

    @Test
    void authenticate_InvalidXmlResponse_ReturnsPartiallyParsedResponse() throws Exception {
        String invalidXml = "<InvalidXml></InvalidXml>";

        mockServer.expect(requestTo(authenticationService.authenticateEndpointUrl))
                .andRespond(withSuccess(invalidXml, MediaType.TEXT_XML));

        AuthenticateResponse response = authenticationService.authenticate("user", "pass");

        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getUsername());
        
    }

    @Test
    void authenticate_RestTemplateThrowsException_ReturnsNull() throws Exception {
        // Simulate a network error (e.g., connection refused)
        mockServer.expect(requestTo(authenticationService.authenticateEndpointUrl))
                .andRespond(withBadRequest());

        AuthenticateResponse response = authenticationService.authenticate("user", "invalid");

        assertNull(response);
        mockServer.verify();
    }
}