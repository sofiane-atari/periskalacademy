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
import periskal.academy.backend.service.AuthenticationService;
import periskal.academy.backend.service.DongleAuthenticationService;
import periskal.academy.backend.util.JwtUtil;



@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {


    private final DongleAuthenticationService dongleAuthenticationService;

    public AuthenticationController(DongleAuthenticationService dongleAuthenticationService) {
        this.dongleAuthenticationService = dongleAuthenticationService;
    }

    @GetMapping("/decode-token")
    public ResponseEntity<AuthenticateResponse> decodeToken(@RequestParam String login) throws Exception {

        AuthenticateResponse userInfo = dongleAuthenticationService.authenticateDongle(login);

        if (userInfo != null && "SuccesByToken".equalsIgnoreCase(userInfo.getLoginResult())) {
            return ResponseEntity.ok(userInfo);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@RequestBody LoginRequest loginRequest) throws Exception {
        AuthenticateResponse response = authenticationService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        
        //System.out.println("LoginResult: " + response.getLoginResult());
        //System.out.println("Authentication Response: " + response);
        

        if (response != null && "SuccesByToken".equalsIgnoreCase(response.getLoginResult())) {
            String token = jwtUtil.generateToken(loginRequest.getUsername());
            //System.out.println("Generated JWT Token: " + token);
            response.setToken(token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }



}


