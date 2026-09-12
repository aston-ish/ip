package topaz;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command processing used by the graphical user interface.
 */
class TopazTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_addThenList_returnsTaskDetails() {
        Topaz topaz = new Topaz(temporaryDirectory.resolve("Topaz.txt"));

        String addResponse = topaz.getResponse("todo read book");
        String listResponse = topaz.getResponse("list");

        assertTrue(addResponse.contains("I've added this task"));
        assertTrue(listResponse.contains("[T][ ] read book"));
    }

    @Test
    void getResponse_invalidCommand_returnsErrorMessage() {
        Topaz topaz = new Topaz(temporaryDirectory.resolve("Topaz.txt"));

        String response = topaz.getResponse("not a command");

        assertTrue(response.contains("I don't know what that means"));
    }

    @Test
    void getResponse_bye_marksSessionAsEnded() {
        Topaz topaz = new Topaz(temporaryDirectory.resolve("Topaz.txt"));

        String response = topaz.getResponse("bye");

        assertTrue(response.contains("Hope to see you again soon"));
        assertTrue(topaz.isExitRequested());
    }
}
