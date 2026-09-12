package topaz.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import topaz.TopazException;
import topaz.task.Deadline;
import topaz.task.Event;
import topaz.task.FixedDurationTask;
import topaz.task.Task;
import topaz.task.Todo;
import topaz.util.DateTimeParser;

/**
 * Loads tasks from and saves tasks to the configured data file.
 */
public class Storage {
    private static final Pattern POSITIVE_DURATION_PATTERN = Pattern.compile("[1-9]\\d*");
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
            task = new Deadline(values[2], DateTimeParser.parse(values[3], "Unable to load a saved task."),
                    DateTimeParser.hasTimeComponent(values[3]));
        } else if (values.length == 5 && values[0].equals("E")) {
            task = new Event(values[2], DateTimeParser.parse(values[3], "Unable to load a saved task."),
                    DateTimeParser.parse(values[4], "Unable to load a saved task."),
                    DateTimeParser.hasTimeComponent(values[3]), DateTimeParser.hasTimeComponent(values[4]));
        } else if (values.length == 4 && values[0].equals("F")) {
            task = new FixedDurationTask(values[2], parseDurationMinutes(values[3]));
        } else {
            throw new TopazException("Unable to load a saved task.");
        }

        if (values[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses a positive whole-minute duration stored in a fixed-duration task.
     */
    private long parseDurationMinutes(String durationText) throws TopazException {
        if (!POSITIVE_DURATION_PATTERN.matcher(durationText).matches()) {
            throw new TopazException("Unable to load a saved task.");
        }
        try {
            long durationMinutes = Long.parseLong(durationText);
            return durationMinutes;
        } catch (NumberFormatException exception) {
            throw new TopazException("Unable to load a saved task.");
        }
    }

}
