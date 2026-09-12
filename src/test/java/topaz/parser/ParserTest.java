package topaz.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import topaz.TopazException;
import topaz.command.AddCommand;
import topaz.command.Command;
import topaz.command.ExitCommand;
import topaz.command.FindCommand;
import topaz.command.ListCommand;

/** Tests the command parser's command recognition and input validation. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_exit_returnsExitCommand() throws TopazException {
        assertInstanceOf(ExitCommand.class, parser.parse("bye", 0));
    }

    @Test
    void parse_list_returnsListCommand() throws TopazException {
        assertInstanceOf(ListCommand.class, parser.parse("list", 0));
    }

    @Test
    void parse_find_returnsFindCommand() throws TopazException {
        assertInstanceOf(FindCommand.class, parser.parse("find book", 0));
    }

    @Test
    void parse_findWithoutKeyword_throwsException() {
        assertThrows(TopazException.class, () -> parser.parse("find", 0));
    }

    @Test
    void parse_todo_returnsAddCommand() throws TopazException {
        assertInstanceOf(AddCommand.class, parser.parse("todo read book", 0));
    }

    @Test
    void parse_durationWithHours_returnsAddCommand() throws TopazException {
        assertInstanceOf(AddCommand.class, parser.parse("duration read report /for 2h", 0));
    }

    @Test
    void parse_durationWithMinutes_returnsAddCommand() throws TopazException {
        assertInstanceOf(AddCommand.class, parser.parse("duration take a break /for 15m", 0));
    }

    @Test
    void parse_durationWithInvalidValues_throwsException() {
        assertDurationError("duration task /for 0h",
                "Use a duration as a positive whole number followed by h or m.");
        assertDurationError("duration task /for -2h",
                "Use a duration as a positive whole number followed by h or m.");
        assertDurationError("duration task /for 1.5h",
                "Use a duration as a positive whole number followed by h or m.");
        assertDurationError("duration task /for 2d",
                "Use a duration as a positive whole number followed by h or m.");
        assertDurationError("duration task /for 1h /for 2h",
                "Use: duration <description> /for <duration>.");
        assertDurationError("duration task /for 1h /for",
                "Use: duration <description> /for <duration>.");
    }

    @Test
    void parse_durationWithoutDescription_throwsException() {
        assertDurationError("duration /for 2h", "The description of a duration task cannot be empty.");
    }

    @Test
    void parse_durationWithoutMarkerOrValue_throwsException() {
        assertDurationError("duration", "Use: duration <description> /for <duration>.");
        assertDurationError("duration read report", "Use: duration <description> /for <duration>.");
        assertDurationError("duration read report /for", "The duration cannot be empty.");
    }

    @Test
    void parse_deadlineWithDateTime_returnsAddCommand() throws TopazException {
        Command command = parser.parse("deadline submit report /by 2/12/2019 1800", 0);
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parse_eventWithDateOnly_returnsAddCommand() throws TopazException {
        Command command = parser.parse("event project meeting /from 2019-10-15 /to 2019-10-16", 0);
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parse_markWithNonInteger_throwsException() {
        assertThrows(TopazException.class, () -> parser.parse("mark two", 2));
    }

    @Test
    void parse_markOutOfRange_throwsException() {
        assertThrows(TopazException.class, () -> parser.parse("mark 3", 2));
    }

    @Test
    void parse_unknownCommand_throwsException() {
        assertThrows(TopazException.class, () -> parser.parse("unknown", 0));
    }

    @Test
    void parse_commandPrefixWithoutSeparator_throwsException() {
        assertThrows(TopazException.class, () -> parser.parse("findbook", 0));
    }

    @Test
    void parse_deadlineWithInvalidDate_throwsException() {
        assertThrows(TopazException.class,
                () -> parser.parse("deadline submit report /by 31/2/2019 1800", 0));
    }

    @Test
    void parse_eventMissingTime_throwsException() {
        assertThrows(TopazException.class,
                () -> parser.parse("event meeting /from 2019-10-15 /to", 0));
    }

    private void assertDurationError(String command, String expectedMessage) {
        TopazException exception = assertThrows(TopazException.class, () -> parser.parse(command, 0));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
