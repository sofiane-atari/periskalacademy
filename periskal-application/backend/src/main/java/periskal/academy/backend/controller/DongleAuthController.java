package periskal.academy.backend.controller;

import org.springframework.web.bind.annotation.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class DongleAuthController {

    private static final Map<Integer, String> DAY_KEYS = new HashMap<>() {
        {
            put(0, "SundayKey123");
            put(1, "MondayKey456");
            put(2, "TuesdayKey789");
            put(3, "WednesdayKeyABC");
            put(4, "ThursdayKeyDEF");
            put(5, "FridayKeyGHI");
            put(6, "SaturdayKeyJKL");
        }
    };

    @PostMapping("/login")
    public String authenticateUser(@RequestParam String encryptedDongle) {
        try {
            // 1. Decrypt dongle key
            String dongleKey = decryptDongle(encryptedDongle);

            // 2. Send SOAP request to authenticate dongle
            String response = sendSoapRequest(dongleKey);

            // 3. Parse and validate response
            if (isValidUser(response)) {
                return "Login successful";
            } else {
                return "Invalid dongle";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String decryptDongle(String encryptedData) throws Exception {
        int dayOfWeek = new Date().getDay();
        String dayKey = DAY_KEYS.get(dayOfWeek);
        byte[] keyBytes = dayKey.getBytes();

        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        keyBytes = sha.digest(keyBytes);
        keyBytes = java.util.Arrays.copyOf(keyBytes, 16);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        return new String(cipher.doFinal(decodedBytes));
    }

    private String sendSoapRequest(String dongleKey) {
        // Simuleer een SOAP request (in productie zou je een echte SOAP client
        // gebruiken)
        return "<Authenticate_DongleResponse><Authenticate_DongleResult><LoginResult>Success</LoginResult></Authenticate_DongleResult></Authenticate_DongleResponse>";
    }

    private boolean isValidUser(String response) {
        return response.contains("<LoginResult>Success</LoginResult>");
    }
}
