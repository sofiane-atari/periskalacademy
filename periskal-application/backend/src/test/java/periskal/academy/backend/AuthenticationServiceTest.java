package periskal.academy.backend;

import static org.assertj.core.api.Assertions.*;
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

        Field restTemplateField = AuthenticationService.class.getSuperclass().getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplate = (RestTemplate) restTemplateField.get(authenticationService);

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    private String getAuthenticateEndpointUrl() throws Exception {
        Field endpointUrlField = AuthenticationService.class.getSuperclass()
                .getDeclaredField("authenticateEndpointUrl");
        endpointUrlField.setAccessible(true);
        return (String) endpointUrlField.get(authenticationService);
    }

    @Test
    void givenValidSoapResponse_whenAuthenticate_thenReturnsParsedAuthResponse() throws Exception {
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

        try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
            mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                    .thenReturn("encoded_token");

            mockServer.expect(requestTo(getAuthenticateEndpointUrl()))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(header("Content-Type", "application/soap+xml;charset=utf-8"))
                    .andRespond(withSuccess(sampleResponseXml, MediaType.TEXT_XML));

            AuthenticateResponse response = authenticationService.authenticate("user", "pass");

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("12345");
            assertThat(response.getUsername()).isEqualTo("testUser");
            assertThat(response.getFirstname()).isEqualTo("John");
            assertThat(response.getLastname()).isEqualTo("Doe");
            assertThat(response.getShipname()).isEqualTo("TestShip");
            assertThat(response.getRole()).isEqualTo("Admin");
            assertThat(response.getLoginResult()).isEqualTo("Success");
            assertThat(response.getProducts()).containsExactly("ProductA", "ProductB");

            mockServer.verify();
        }
    }

    @Test
    void givenHttpError_whenAuthenticate_thenReturnsNull() throws Exception {
        try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
            mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                    .thenReturn("encoded_token");

            mockServer.expect(requestTo(getAuthenticateEndpointUrl()))
                    .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

            AuthenticateResponse response = authenticationService.authenticate("user", "pass");

            assertThat(response).isNull();
            mockServer.verify();
        }
    }

    @Test
    void givenInvalidXmlResponse_whenAuthenticate_thenReturnsPartiallyParsedResponse() throws Exception {
        String invalidXml = "<InvalidXml></InvalidXml>";

        try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
            mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                    .thenReturn("encoded_token");

            mockServer.expect(requestTo(getAuthenticateEndpointUrl()))
                    .andRespond(withSuccess(invalidXml, MediaType.TEXT_XML));

            AuthenticateResponse response = authenticationService.authenticate("user", "pass");

            assertThat(response).isNotNull();
            assertThat(response.getId()).isNull();
            assertThat(response.getUsername()).isNull();
            mockServer.verify();
        }
    }

    @Test
    void givenRestTemplateThrowsException_whenAuthenticate_thenReturnsNull() throws Exception {
        try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
            mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                    .thenReturn("encoded_token");

            mockServer.expect(requestTo(getAuthenticateEndpointUrl()))
                    .andRespond(withBadRequest());

            AuthenticateResponse response = authenticationService.authenticate("user", "invalid");

            assertThat(response).isNull();
            mockServer.verify();
        }
    }

    @Test
    void givenEncryptionUtilThrowsException_whenAuthenticate_thenReturnsNull() throws Exception {
        try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
            mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                    .thenThrow(new RuntimeException("Encryption failed"));

            AuthenticateResponse response = authenticationService.authenticate("user", "pass");

            assertThat(response).isNull();
        }
    }
}
