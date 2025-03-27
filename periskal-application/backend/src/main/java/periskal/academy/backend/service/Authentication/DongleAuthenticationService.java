package periskal.academy.backend.service.Authentication;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.model.AuthenticateResponse;

@Slf4j
@Service
public class DongleAuthenticationService extends BaseAuthenticationService {

    @Override
    protected String buildSoapRequest(String token) {
        // Replace spaces with '+' as required.
        token = token.replace(" ", "+");
        return "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
               "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
               "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
               "<soap12:Body>" +
               "<Authenticate_Dongle xmlns=\"http://tempuri.org/\">" +
               "<Dongle>" + token + "</Dongle>" +
               "</Authenticate_Dongle>" +
               "</soap12:Body>" +
               "</soap12:Envelope>";
    }

    /**
     * Public method for dongle authentication.
     */
    public AuthenticateResponse authenticateDongle(String token) {
        return super.authenticate(token);
    }
}
