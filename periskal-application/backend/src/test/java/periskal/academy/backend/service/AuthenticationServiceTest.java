package periskal.academy.backend.service;

import java.lang.reflect.Field;
import java.net.SocketTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationCommunicationException;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationInvalidCredentialsException;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationParsingException;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationServiceException;
import periskal.academy.backend.service.authentication.AuthenticationService;
import periskal.academy.backend.util.EncryptionUtil;

class AuthenticationServiceTest {

        private AuthenticationService authenticationService;
        private MockRestServiceServer mockServer;
        private RestTemplate restTemplate;
        private String authenticateEndpointUrl;

        @BeforeEach
        void setUp() throws Exception {
                authenticationService = new AuthenticationService();

                Field restTemplateField = AuthenticationService.class.getSuperclass().getDeclaredField("restTemplate");
                restTemplateField.setAccessible(true);
                restTemplate = (RestTemplate) restTemplateField.get(authenticationService);

                Field endpointUrlField = AuthenticationService.class.getSuperclass()
                                .getDeclaredField("AUTHENTICATE_ENDPOINT_URL");
                endpointUrlField.setAccessible(true);
                authenticateEndpointUrl = (String) endpointUrlField.get(null);

                mockServer = MockRestServiceServer.createServer(restTemplate);
        }

        @Test
        void givenValidSoapResponse_whenAuthenticate_thenReturnsParsedAuthResponse() {
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

                        mockServer.expect(requestTo(authenticateEndpointUrl))
                                        .andExpect(method(HttpMethod.POST))
                                        .andExpect(header("Content-Type", "application/soap+xml;charset=utf-8"))
                                        .andRespond(withSuccess(sampleResponseXml, MediaType.TEXT_XML));

                        // act
                        AuthenticateResponse response = authenticationService.authenticate("user", "pass");

                        // assert
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
        void givenInvalidCredentialsSoapResponse_whenAuthenticate_thenThrowsAuthenticationInvalidCredentialsException() {
                String invalidCredsXml = """
                                <?xml version="1.0" encoding="utf-8"?>
                                <soap12:Envelope xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
                                  <soap12:Body>
                                    <AuthenticateResponse>
                                      <AuthenticateResult>
                                        <ID></ID>
                                        <Username></Username>
                                        <LoginResult></LoginResult>
                                      </AuthenticateResult>
                                    </AuthenticateResponse>
                                  </soap12:Body>
                                </soap12:Envelope>""";

                try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
                        mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                                        .thenReturn("encoded_token");

                        mockServer.expect(requestTo(authenticateEndpointUrl))
                                        .andRespond(withSuccess(invalidCredsXml, MediaType.APPLICATION_XML));

                        // Act & Assert
                        assertThrows(
                                        AuthenticationInvalidCredentialsException.class,
                                        () -> authenticationService.authenticate("user", "wrongpass"));

                        mockServer.verify();
                }
        }

        @Test
        void givenHttpError_whenAuthenticate_thenThrowsAuthenticationServerErrorException() {
                try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
                        mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                                        .thenReturn("encoded_token");

                        mockServer.expect(requestTo(authenticateEndpointUrl))
                                        .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

                        assertThrows(
                                        AuthenticationCommunicationException.class,
                                        () -> authenticationService.authenticate("user", "pass"));

                        mockServer.verify();

                }
        }

        @Test
        void givenMalformedXmlResponse_whenAuthenticate_thenThrowsAuthenticationParsingException() {
                String malformedXml = "<MalformedXml><Oops></MalformedXml>"; // Missing closing tag

                try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
                        mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                                        .thenReturn("encoded_token");

                        mockServer.expect(requestTo(authenticateEndpointUrl))
                                        .andRespond(withSuccess(malformedXml, MediaType.APPLICATION_XML));

                        // Act & Assert
                        assertThrows(
                                        AuthenticationParsingException.class,
                                        () -> authenticationService.authenticate("user", "pass"));

                        mockServer.verify();
                }
        }

        @Test
        void givenRestTemplateThrowsRestClientException_whenAuthenticate_thenThrowsAuthenticationCommunicationException() {
                try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
                        mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                                        .thenReturn("encoded_token");

                        mockServer.expect(requestTo(authenticateEndpointUrl))
                                        .andRespond(request -> {
                                                throw new ResourceAccessException("I/O error",
                                                                new SocketTimeoutException("Timeout!"));
                                        });

                        // Act & assert
                        assertThrows(
                                        AuthenticationCommunicationException.class,
                                        () -> authenticationService.authenticate("user", "pass"));

                        mockServer.verify();
                }
        }

        @Test
        void givenEncryptionUtilThrowsException_whenAuthenticate_thenThrowsAuthenticationServiceException() {
                RuntimeException encryptionException = new RuntimeException("Encryption failed");

                try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
                        mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                                        .thenThrow(encryptionException); // Mock EncryptionUtil to throw

                        // Act & Assert
                        assertThrows(
                                        AuthenticationServiceException.class,
                                        () -> authenticationService.authenticate("user", "pass"));

                }
        }

        @Test
        void givenSoapResponseWithSkippers_whenAuthenticate_thenParsesSkippersCorrectly() {
                String soapResponseWithSkippers = """
                                <?xml version="1.0" encoding="utf-8"?>
                                <soap12:Envelope xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
                                  <soap12:Body>
                                    <AuthenticateResponse xmlns="http://tempuri.org/">
                                      <AuthenticateResult>
                                        <ID>12345</ID>
                                        <Username>testUser</Username>
                                        <LoginResult>Success</LoginResult>
                                        <Skippers>
                                          <Client>
                                            <ID>1</ID>
                                            <Username>skipper1</Username>
                                            <Firstname>Anne</Firstname>
                                            <Lastname>Belle</Lastname>
                                            <Shipname>Neptunus</Shipname>
                                            <Role>Captain</Role>
                                            <Products>
                                              <string>Product1</string>
                                              <string>Product2</string>
                                            </Products>
                                          </Client>
                                          <Client>
                                            <ID>2</ID>
                                            <Username>skipper2</Username>
                                            <Firstname>Jos</Firstname>
                                            <Lastname>VanHoof</Lastname>
                                            <Shipname>Neptunus</Shipname>
                                            <Role>Matroos</Role>
                                            <Products>
                                              <string>Product1</string>
                                              <string>Product2</string>
                                            </Products>
                                          </Client>
                                        </Skippers>
                                      </AuthenticateResult>
                                    </AuthenticateResponse>
                                  </soap12:Body>
                                </soap12:Envelope>""";

                try (MockedStatic<EncryptionUtil> mockedStatic = Mockito.mockStatic(EncryptionUtil.class)) {
                        mockedStatic.when(() -> EncryptionUtil.encode(anyString(), anyString()))
                                        .thenReturn("encoded_token");

                        mockServer.expect(requestTo(authenticateEndpointUrl))
                                        .andRespond(withSuccess(soapResponseWithSkippers, MediaType.APPLICATION_XML));

                        AuthenticateResponse response = authenticationService.authenticate("user", "pass");

                        assertThat(response.getSkippers()).hasSize(2);
                        assertThat(response.getSkippers().get(0).getUsername()).isEqualTo("skipper1");
                        assertThat(response.getSkippers().get(0).getProducts()).containsExactly("Product1", "Product2");
                        assertThat(response.getSkippers().get(1).getId()).isEqualTo("2");
                }
        }

}
