package periskal.academy.backend.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class EncryptionUtilTest {

    @Test
    public void givenUsernameAndPassword_whenEncode_thenReturnsExpectedToken() {
        String username = "Demo";
        String password = "Demo1979!";
        String expectedToken = "cCt0azhyNkdyRlFrQmc4cTFubGh2Zz09";

        // Using AssertJ for assertions
        assertThatCode(() -> {
            String actualToken = EncryptionUtil.encode(username, password);
            assertThat(actualToken).isEqualTo(expectedToken);
        }).doesNotThrowAnyException();
    }
}
