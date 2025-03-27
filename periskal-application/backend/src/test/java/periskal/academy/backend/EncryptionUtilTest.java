package periskal.academy.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import periskal.academy.backend.util.EncryptionUtil;

public class EncryptionUtilTest {

    @Test
    public void givenUsernameAndPassword_whenEncode_thenReturnsExpectedToken() {
        String username = "Demo";
        String password = "Demo1979!";
        String expectedToken = "cCt0azhyNkdyRlFrQmc4cTFubGh2Zz09";

        try {
            String actualToken = EncryptionUtil.encode(username, password);
            assertEquals(expectedToken, actualToken, "The encoded token does not match the expected value.");
        } catch (Exception e) {
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }
}