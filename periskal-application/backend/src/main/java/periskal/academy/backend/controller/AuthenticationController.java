package periskal.academy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.model.LoginRequest;
import periskal.academy.backend.service.authentication.AuthenticationService;
import periskal.academy.backend.service.authentication.DongleAuthenticationService;
import periskal.academy.backend.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final DongleAuthenticationService dongleAuthenticationService;
    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;

    public AuthenticationController(DongleAuthenticationService dongleAuthenticationService,
            AuthenticationService authenticationService
            , JwtUtil jwtUtil) {
        this.dongleAuthenticationService = dongleAuthenticationService;
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@RequestBody LoginRequest loginRequest) {

        // 1. Authenticate using the authentication service
        AuthenticateResponse response = authenticationService.authenticate(loginRequest.getUsername(),
                loginRequest.getPassword());

        // 2. If successful, generate a JWT token and return it in the response
        String token = jwtUtil.generateToken(response);
        response.setToken(token);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/decode-token")
    public ResponseEntity<AuthenticateResponse> decodeToken(@RequestParam String login) {

        AuthenticateResponse responseD = dongleAuthenticationService.authenticateDongle(login);

        String token = jwtUtil.generateToken(responseD);
        responseD.setToken(token);
        return ResponseEntity.ok(responseD);

    }
}
