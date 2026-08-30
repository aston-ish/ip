package topaz.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import topaz.TopazException;
import topaz.command.AddCommand;
import topaz.command.Command;
import topaz.command.ExitCommand;
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
    void parse_todo_returnsAddCommand() throws TopazException {
        assertInstanceOf(AddCommand.class, parser.parse("todo read book", 0));
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
    void parse_deadlineWithInvalidDate_throwsException() {
        assertThrows(TopazException.class,
                () -> parser.parse("deadline submit report /by 31/2/2019 1800", 0));
    }

    @Test
    void parse_eventMissingTime_throwsException() {
        assertThrows(TopazException.class,
                () -> parser.parse("event meeting /from 2019-10-15 /to", 0));
    }
}
