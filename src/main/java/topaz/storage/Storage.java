package topaz.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import topaz.TopazException;
import topaz.task.Deadline;
import topaz.task.Event;
import topaz.task.Task;
import topaz.task.Todo;

/**
 * Loads tasks from and saves tasks to the configured data file.
 */
public class Storage {
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private final Path saveFile;

    /**
     * Creates storage that uses the given file path.
     *
     * @param saveFile the path of the task data file
     */
    public Storage(Path saveFile) {
        this.saveFile = saveFile;
    }

    /**
     * Saves every task in the current list to the data file.
     *
     * @param tasks the tasks to save
     * @throws TopazException if the data file cannot be written
     */
    public void save(List<Task> tasks) throws TopazException {
        try {
            Path parentDirectory = saveFile.getParent();
            if (parentDirectory != null && Files.exists(parentDirectory)
                    && !Files.isDirectory(parentDirectory)) {
                throw new TopazException("The data directory path is not a directory.");
            }
            if (parentDirectory != null && !Files.exists(parentDirectory)) {
                Files.createDirectories(parentDirectory);
            }

            try (PrintWriter writer = new PrintWriter(
                    new FileWriter(saveFile.toFile(), StandardCharsets.UTF_8))) {
                for (Task task : tasks) {
                    writer.println(task.toFileString());
                }
                if (writer.checkError()) {
                    throw new IOException("Unable to write the save file.");
                }
            }
        } catch (IOException exception) {
            throw new TopazException("Unable to save your tasks.");
        } catch (SecurityException exception) {
            throw new TopazException("Unable to access the save file.");
        }
    }

    /**
     * Loads saved tasks, returning an empty list when the data file does not exist.
     *
     * @return the tasks loaded from the data file
     * @throws TopazException if the data file cannot be read or contains invalid data
     */
    public List<Task> load() throws TopazException {
        List<Task> tasks = new ArrayList<>();
        try {
            if (!Files.exists(saveFile)) {
                return tasks;
            }
            if (!Files.isRegularFile(saveFile)) {
                throw new TopazException("The save file path is not a file.");
            }

            try (Scanner fileScanner = new Scanner(saveFile, StandardCharsets.UTF_8)) {
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (!line.isBlank()) {
                        tasks.add(createTask(line));
                    }
                }
                if (fileScanner.ioException() != null) {
                    throw new TopazException("Unable to load your saved tasks.");
                }
            }
        } catch (IOException | SecurityException exception) {
            throw new TopazException("Unable to load your saved tasks.");
        }
        return tasks;
    }

    /**
     * Reconstructs one task from a line in the data file.
     */
    private Task createTask(String line) throws TopazException {
        String[] values = line.split(" \\| ", -1);
        if (values.length < 3 || (!values[1].equals("0") && !values[1].equals("1"))) {
            throw new TopazException("Unable to load a saved task.");
        }
        for (int i = 2; i < values.length; i++) {
            if (values[i].isBlank() || values[i].contains("|")) {
                throw new TopazException("Unable to load a saved task.");
            }
        }

        Task task;
        if (values.length == 3 && values[0].equals("T")) {
            task = new Todo(values[2]);
        } else if (values.length == 4 && values[0].equals("D")) {
            task = new Deadline(values[2], parseDateTime(values[3]), hasTimeComponent(values[3]));
        } else if (values.length == 5 && values[0].equals("E")) {
            task = new Event(values[2], parseDateTime(values[3]), parseDateTime(values[4]),
                    hasTimeComponent(values[3]), hasTimeComponent(values[4]));
        } else {
            throw new TopazException("Unable to load a saved task.");
        }

        if (values[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses a date value stored in the data file.
     */
    private LocalDateTime parseDateTime(String text) throws TopazException {
        try {
            return LocalDateTime.parse(text, DATE_TIME_INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            try {
                return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            } catch (DateTimeParseException ignoredException) {
                try {
                    return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (DateTimeParseException ignoredAgainException) {
                    throw new TopazException("Unable to load a saved task.");
                }
            }
        }
    }

    /**
     * Returns whether a stored date value includes a time component.
     */
    private boolean hasTimeComponent(String text) {
        return !text.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
