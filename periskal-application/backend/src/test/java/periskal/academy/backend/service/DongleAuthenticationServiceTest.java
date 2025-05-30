package periskal.academy.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import periskal.academy.backend.service.authentication.BaseAuthenticationService;
import periskal.academy.backend.service.authentication.DongleAuthenticationService;

class DongleAuthenticationServiceTest {

    @InjectMocks
    private DongleAuthenticationService dongleAuthenticationService;

    @Mock
    private BaseAuthenticationService baseAuthenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuildSoapRequest_withSpacesInToken() {
        // Test that spaces in the token are replaced with "+"
        String token = "token with spaces";
        String expectedRequest = "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap12:Body>" +
                "<Authenticate_Dongle xmlns=\"http://tempuri.org/\">" +
                "<Dongle>token+with+spaces</Dongle>" +
                "</Authenticate_Dongle>" +
                "</soap12:Body>" +
                "</soap12:Envelope>";

        String soapRequest = dongleAuthenticationService.buildSoapRequest(token);
        assertEquals(expectedRequest, soapRequest);
    }
}
