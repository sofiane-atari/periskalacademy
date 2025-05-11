package periskal.academy.backend.service.authentication;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationServiceException;
import periskal.academy.backend.util.EncryptionUtil;

@Slf4j
@Service
public class AuthenticationService extends BaseAuthenticationService {

    @Override
    protected String buildSoapRequest(String token) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                "  <soap12:Body>\n" +
                "    <Authenticate xmlns=\"http://tempuri.org/\">\n" +
                "      <Token>" + token + "</Token>\n" +
                "    </Authenticate>\n" +
                "  </soap12:Body>\n" +
                "</soap12:Envelope>";
    }

    /**
     * Public method for username/password authentication.
     */
    public AuthenticateResponse authenticate(String username, String password) {

        String token;

        try {
            token = EncryptionUtil.encode(username, password);
        } catch (Exception encryptionEx) {
            log.error("Internal error: Failed to encode credentials for user: {}", username, encryptionEx);
            throw new AuthenticationServiceException("Internal authentication process error during credential encoding",
                    encryptionEx);
        }

        return super.authenticate(token);

    }
}
