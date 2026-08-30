package topaz.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import topaz.TopazException;
import topaz.task.Deadline;
import topaz.task.Event;
import topaz.task.Task;
import topaz.task.Todo;

/** Tests persistence, reconstruction, and invalid save-file handling. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyList() throws TopazException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesDetailsAndStatus() throws TopazException {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("Topaz.txt"));
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0), true);
        Task event = new Event("project meeting", LocalDateTime.of(2019, 10, 15, 0, 0),
                LocalDateTime.of(2019, 10, 16, 0, 0), false, false);
        deadline.markAsDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertFalse(loaded.get(0).isDone());
        assertEquals("return book (by: Dec 02 2019 1800)", loaded.get(1).getDescription());
        assertTrue(loaded.get(1).isDone());
        assertEquals("project meeting (from: Oct 15 2019 to: Oct 16 2019)",
                loaded.get(2).getDescription());
    }

    @Test
    void load_malformedRecord_throwsException() throws IOException {
        Path file = temporaryDirectory.resolve("malformed.txt");
        Files.writeString(file, "X | 0 | invalid task\n");

        assertThrows(TopazException.class, () -> new Storage(file).load());
    }

    @Test
    void load_invalidStatus_throwsException() throws IOException {
        Path file = temporaryDirectory.resolve("invalid-status.txt");
        Files.writeString(file, "T | 2 | read book\n");

        assertThrows(TopazException.class, () -> new Storage(file).load());
    }
}
