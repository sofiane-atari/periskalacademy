package periskal.academy.backend.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class CryptoService {

    private static final String[] DAY_KEYS = {
            "SundayKey123", "MondayKey456", "TuesdayKey789",
            "WednesdayKeyABC", "ThursdayKeyDEF", "FridayKeyGHI", "SaturdayKeyJKL"
    };

    public String decode(String encodedString) throws Exception {
        String[] parts = encodedString.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encoded string format.");
        }

        String hashPrefix = parts[0];
        int remainder = Integer.parseInt(parts[1]);

        LocalDate currentDate = LocalDate.now();
        int dayOfWeek = currentDate.getDayOfWeek().getValue() % 7;
        String dayKey = DAY_KEYS[dayOfWeek];

        for (int i = 1; i <= 99999; i++) {
            if (i % 100000 == remainder) {
                // ✅ Call encode as an instance method
                String testEncode = this.encode(i, currentDate, dayKey);
                if (testEncode.startsWith(hashPrefix)) {
                    return String.format("PER%05d", i);
                }
            }
        }
        throw new IllegalArgumentException("Unable to decode the number.");
    }

    // ✅ Remove static here
    public String encode(int number, LocalDate date, String dayKey) throws Exception {
        String combined = String.format("%05d|%s", number, date.toString());
        byte[] keyBytes = dayKey.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = combined.getBytes(StandardCharsets.UTF_8);

        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
        hmac.init(keySpec);
        byte[] hashBytes = hmac.doFinal(dataBytes);

        String encodedHash = Base64.getEncoder().encodeToString(hashBytes);
        return encodedHash.substring(0, 8) + ":" + (number % 100000);
    }
}
