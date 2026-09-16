package topaz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
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

        assertTrue(addResponse.contains("Gronk grabbed a new task"));
        assertTrue(listResponse.contains("[T][ ] read book"));
    }

    @Test
    void getResponse_durationTask_supportsExistingTaskCommands() {
        Topaz topaz = new Topaz(temporaryDirectory.resolve("Topaz.txt"));

        String addResponse = topaz.getResponse("duration read report /for 2h");
        String markResponse = topaz.getResponse("mark 1");
        String findResponse = topaz.getResponse("find hours");
        String deleteResponse = topaz.getResponse("delete 1");

        assertTrue(addResponse.contains("[F][ ] read report (for: 2 hours)"));
        assertTrue(markResponse.contains("[F][X] read report (for: 2 hours)"));
        assertTrue(findResponse.contains("[F][X] read report (for: 2 hours)"));
        assertTrue(deleteResponse.contains("[F][X] read report (for: 2 hours)"));
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

        assertTrue(response.contains("Gronk rests now"));
        assertTrue(topaz.isExitRequested());
    }

    @Test
    void getResponse_errorThenSuccess_resetsErrorStatus() {
        Topaz topaz = new Topaz(temporaryDirectory.resolve("Topaz.txt"));
        assertFalse(topaz.isResponseError());
        for (String command : new String[] {"unknown", "todo", "mark 99", "deadline task /by invalid"}) {
            topaz.getResponse(command);
            assertTrue(topaz.isResponseError());
            topaz.getResponse("list");
            assertFalse(topaz.isResponseError());
        }
        topaz.getResponse("todo ERROR is part of this description");
        assertFalse(topaz.isResponseError());
    }

    @Test
    void getResponse_unreadableSaveFile_marksError() {
        Topaz topaz = new Topaz(temporaryDirectory);
        assertTrue(topaz.getResponse("list").contains("not a file"));
        assertTrue(topaz.isResponseError());
        assertTrue(topaz.getResponse("list").startsWith("GRONK!" + System.lineSeparator()));
    }

    @Test
    void getResponse_saveFailureThenRecovery_updatesErrorStatus() throws Exception {
        Path saveFile = temporaryDirectory.resolve("Topaz.txt");
        Topaz topaz = new Topaz(saveFile);
        topaz.getResponse("list");
        Files.createDirectory(saveFile);
        assertTrue(topaz.getResponse("todo read book").contains("Unable to save"));
        assertTrue(topaz.isResponseError());
        Files.delete(saveFile);
        topaz.getResponse("todo read book");
        assertFalse(topaz.isResponseError());
    }

    @Test
    void getResponse_eachCommand_usesGronkBattleCry() {
        Topaz topaz = new Topaz(temporaryDirectory.resolve("Topaz.txt"));
        String[] commands = {"todo lift rocks", "list", "find rocks", "mark 1", "unmark 1",
                "delete 1", "unknown", "bye"};
        for (String command : commands) {
            assertTrue(topaz.getResponse(command).startsWith("GRONK!" + System.lineSeparator()));
        }
    }

    @Test
    void getResponse_duplicateTask_keepsSavedTaskAndCompletion() throws Exception {
        Path saveFile = temporaryDirectory.resolve("Topaz.txt");
        Topaz topaz = new Topaz(saveFile);
        topaz.getResponse("todo Read Book");
        topaz.getResponse("mark 1");
        String saved = Files.readString(saveFile);

        assertTrue(topaz.getResponse("todo  read   book").contains("already exists"));
        assertTrue(topaz.isResponseError());
        assertEquals(saved, Files.readString(saveFile));
        assertTrue(topaz.getResponse("list").contains("1.[T][X] Read Book"));
        assertFalse(topaz.isResponseError());
    }

    @Test
    void getResponse_invalidEventPeriod_doesNotAddTask() {
        Topaz topaz = new Topaz(temporaryDirectory.resolve("Topaz.txt"));
        for (String end : new String[] {"2026-12-06", "2026-12-07"}) {
            assertTrue(topaz.getResponse("event meeting /from 2026-12-07 /to " + end)
                    .contains("end must be after"));
            assertTrue(topaz.isResponseError());
        }
        assertFalse(topaz.getResponse("list").contains("[E]"));
    }

}
