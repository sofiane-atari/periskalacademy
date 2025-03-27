package periskal.academy.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public AuthenticationController(
            DongleAuthenticationService dongleAuthenticationService,
            AuthenticationService authenticationService) {
        this.dongleAuthenticationService = dongleAuthenticationService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@RequestBody LoginRequest loginRequest) {
        AuthenticateResponse response = authenticationService.authenticate(
                loginRequest.getUsername(), loginRequest.getPassword());

        if (response != null && "SuccesByToken".equalsIgnoreCase(response.getLoginResult())) {
            String token = JwtUtil.generateToken(response);
            response.setToken(token);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping("/decode-token")
    public ResponseEntity<AuthenticateResponse> decodeToken(@RequestParam String login) {
        AuthenticateResponse responseD = dongleAuthenticationService.authenticateDongle(login);

        if (responseD != null && "SuccesByToken".equalsIgnoreCase(responseD.getLoginResult())) {
            String token = JwtUtil.generateToken(responseD);
            responseD.setToken(token);
            return ResponseEntity.ok(responseD);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
