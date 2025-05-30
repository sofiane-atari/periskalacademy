package periskal.academy.backend.service;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CryptoServiceTest {

    private CryptoService cryptoService;

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoService();
    }

    @Test
    void givenValidInput_whenEncode_thenReturnsCorrectFormat() throws Exception {
        int number = 12345;
        LocalDate date = LocalDate.of(2024, 5, 13);
        String dayKey = "MondayKey456";

        String encoded = cryptoService.encode(number, date, dayKey);

        assertThat(encoded)
            .isNotNull()
            .contains(":")
            .matches(s -> s.split(":")[1].equals(String.valueOf(number % 100000)));
    }

    @Test
    void givenValidEncodedString_whenDecode_thenReturnsCorrectCode() throws Exception {
        int number = 12345;
        LocalDate today = LocalDate.now();
        int dayOfWeek = today.getDayOfWeek().getValue() % 7;
        String dayKey = getDayKey(dayOfWeek);

        String encoded = cryptoService.encode(number, today, dayKey);

        String decoded = cryptoService.decode(encoded);

        assertThat(decoded).isEqualTo("PER12345");
    }

    @Test
    void givenInvalidFormat_whenDecode_thenThrowsException() {
        String invalid = "InvalidFormatOnlyOnePart";

        assertThatThrownBy(() -> cryptoService.decode(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid encoded string format");
    }

    @Test
    void givenNonMatchingHash_whenDecode_thenThrowsException() {
        String fake = "fakehash:42";

        assertThatThrownBy(() -> cryptoService.decode(fake))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unable to decode the number");
    }

    private String getDayKey(int index) {
        String[] keys = {
                "SundayKey123", "MondayKey456", "TuesdayKey789",
                "WednesdayKeyABC", "ThursdayKeyDEF", "FridayKeyGHI", "SaturdayKeyJKL"
        };
        return keys[index];
    }
}
