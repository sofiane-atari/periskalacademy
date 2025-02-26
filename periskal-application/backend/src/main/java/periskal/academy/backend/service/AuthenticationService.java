package periskal.academy.backend.service;

import org.springframework.stereotype.Service;

import periskal.academy.backend.model.AuthenticateResponse;

@Service
public class AuthenticationService {

    // This will hold logic for encoding credentials, calling the SOAP API, etc.
    public AuthenticateResponse authenticate(String username, String password) {
        // TODO: Implement the logic to handle:
        // 1. Encoding username and password. --> EncryptionUtil.java
        // 2. Creating SOAP request with the encoded credentials.
        // 3. Making the request to the external SOAP service.
        // 4. Parsing the XML response.
        // 5. Returning an AuthenticationResponse.
        
        //return null; // Return appropriate response based on the SOAP response.


        // For now, we'll simulate a successful login response
        AuthenticateResponse response = new AuthenticateResponse();
        response.setId("1234"); // Dummy ID for now
        response.setUsername(username); // Echo the username
        response.setFirstname("jan"); // Jan brom de Hulp 
        response.setLastname("brom");
        response.setRole("Hulp");
        response.setLoginResult("Success"); // Simulate successful login
        
        // Return the dummy response
        return response;

    }
}
