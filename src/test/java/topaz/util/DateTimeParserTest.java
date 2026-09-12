package topaz.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import topaz.TopazException;

/** Tests the date and date-time formats shared by parsing and storage. */
class DateTimeParserTest {
    @Test
    void parse_supportedFormats_returnsExpectedDateTime() throws TopazException {
        assertEquals(LocalDateTime.of(2026, 9, 11, 14, 30),
                DateTimeParser.parse("11/9/2026 1430", "invalid"));
        assertEquals(LocalDateTime.of(2026, 9, 11, 0, 0),
                DateTimeParser.parse("2026-09-11", "invalid"));
        assertEquals(LocalDateTime.of(2026, 9, 11, 14, 30),
                DateTimeParser.parse("2026-09-11T14:30", "invalid"));
    }

    @Test
    void parse_invalidFormat_throwsProvidedErrorMessage() {
        TopazException exception = assertThrows(TopazException.class,
                () -> DateTimeParser.parse("not a date", "Use a valid date."));

        assertEquals("Use a valid date.", exception.getMessage());
    }

    @Test
    void hasTimeComponent_dateOnlyAndDateTime_returnsExpectedValue() {
        assertFalse(DateTimeParser.hasTimeComponent("2026-09-11"));
        assertTrue(DateTimeParser.hasTimeComponent("2026-09-11T14:30"));
    }
}
