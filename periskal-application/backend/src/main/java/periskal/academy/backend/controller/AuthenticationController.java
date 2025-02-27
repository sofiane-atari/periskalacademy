package periskal.academy.backend.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.service.CryptoService;
import periskal.academy.backend.service.DongleAuthenticationService;

// @RestController
// @RequestMapping("/api/auth")
// public class AuthenticationController {

//     private final DongleAuthenticationService dongleAuthenticationService;

//     public AuthenticationController(DongleAuthenticationService dongleAuthenticationService) {
//         this.dongleAuthenticationService = dongleAuthenticationService;
//     }

//     @GetMapping("/decode-token")
//     public ResponseEntity<AuthenticateResponse> decodeToken(@RequestParam String login) throws Exception {
//         // // Step 1: Decrypt the login token
//         // String decryptedLogin = cryptoService.decode(login); // Decode the token

//         // // Step 2: Encode the decrypted token before sending to SOAP API
//         // String encodedLogin =
//         // cryptoService.encode(Integer.parseInt(decryptedLogin.substring(3)),
//         // LocalDate.now(),
//         // "ThursdayKeyDEF"); // Replace with the appropriate day key

//         // Step 3: Authenticate with the external SOAP service
//         AuthenticateResponse userInfoJson = dongleAuthenticationService.authenticateDongle(login);

//         // AuthenticateResponse userInfo = objectMapper.treeToValue(userInfoJson,
//         // AuthenticateResponse.class);

//         if (userInfoJson != null && "SuccesByToken".equalsIgnoreCase(userInfoJson.getLoginResult())) {
//             return ResponseEntity.ok(userInfoJson);
//         } else {
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//         }
//     }

// }
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
}
