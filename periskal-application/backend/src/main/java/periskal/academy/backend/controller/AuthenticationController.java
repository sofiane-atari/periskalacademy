package periskal.academy.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.model.LoginRequest;
import periskal.academy.backend.service.Authentication.AuthenticationService;
import periskal.academy.backend.service.Authentication.DongleAuthenticationService;
import periskal.academy.backend.util.JwtUtil;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final DongleAuthenticationService dongleAuthenticationService;
    private final AuthenticationService authenticationService;
    @Autowired
    public AuthenticationController(
            DongleAuthenticationService dongleAuthenticationService,
            AuthenticationService authenticationService,
            JwtUtil jwtUtil) {
        this.dongleAuthenticationService = dongleAuthenticationService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@RequestBody LoginRequest loginRequest) throws Exception {
        AuthenticateResponse response = authenticationService.authenticate(loginRequest.getUsername(),
                loginRequest.getPassword());

        // System.out.println("LoginResult: " + response.getLoginResult());
        // System.out.println("Authentication Response: " + response);

        if (response != null && "SuccesByToken".equalsIgnoreCase(response.getLoginResult())) {
            String token = JwtUtil.generateToken(response);
            //System.out.println("Generated JWT Token: " + token);
            response.setToken(token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    
    @GetMapping("/decode-token")
    public ResponseEntity<AuthenticateResponse> decodeToken(@RequestParam String login) throws Exception {

        AuthenticateResponse responseD = dongleAuthenticationService.authenticateDongle(login);

        if (responseD != null && "SuccesByToken".equalsIgnoreCase(responseD.getLoginResult())) {
            String token = JwtUtil.generateToken(responseD);
            responseD.setToken(token);
            return ResponseEntity.ok(responseD);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
