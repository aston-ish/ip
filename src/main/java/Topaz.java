import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.io.PrintWriter;

public class Topaz {
    private static final Path DEFAULT_SAVE_FILE = Paths.get("data", "Topaz.txt");
    private static final Path SAVE_FILE = Paths.get(
            System.getProperty("topaz.dataFile", DEFAULT_SAVE_FILE.toString()));
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private final Ui ui;

    /** Creates Topaz with a console user interface. */
    public Topaz() {
        ui = new Ui();
    }

    /**
     * Saves every task in the current list to the hard disk.
     *
     * @param tasks the tasks to save
     * @throws TopazException if the save file cannot be created or written
     */
    private static void saveTasks(List<Task> tasks) throws TopazException {
        try {
            Path parentDirectory = SAVE_FILE.getParent();
            if (parentDirectory != null && Files.exists(parentDirectory)
                    && !Files.isDirectory(parentDirectory)) {
                throw new TopazException("The data directory path is not a directory.");
            }
            if (parentDirectory != null && !Files.exists(parentDirectory)) {
                Files.createDirectories(parentDirectory);
            }

            try (PrintWriter writer = new PrintWriter(
                    new FileWriter(SAVE_FILE.toFile(), StandardCharsets.UTF_8))) {
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
     * Loads the saved tasks, if the save file already exists.
     *
     * @return the tasks reconstructed from the save file
     * @throws TopazException if the save file cannot be read
     */
    private static List<Task> loadTasks() throws TopazException {
        List<Task> tasks = new ArrayList<>();
        try {
            if (!Files.exists(SAVE_FILE)) {
                return tasks;
            }
            if (!Files.isRegularFile(SAVE_FILE)) {
                throw new TopazException("The save file path is not a file.");
            }

            try (Scanner fileScanner = new Scanner(SAVE_FILE, StandardCharsets.UTF_8)) {
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
     * Reconstructs one task from a line in the save file.
     *
     * @param line one task record
     * @return the reconstructed task
     * @throws TopazException if the record has an unsupported format
     */
    private static Task createTask(String line) throws TopazException {
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
            task = new Deadline(values[2], parseDateTime(values[3], "Unable to load a saved task."),
                    hasTimeComponent(values[3]));
        } else if (values.length == 5 && values[0].equals("E")) {
            task = new Event(values[2], parseDateTime(values[3], "Unable to load a saved task."),
                    parseDateTime(values[4], "Unable to load a saved task."),
                    hasTimeComponent(values[3]), hasTimeComponent(values[4]));
        } else {
            throw new TopazException("Unable to load a saved task.");
        }

        if (values[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Parses a supported date or date-time into a value that can be compared and saved reliably. */
    private static LocalDateTime parseDateTime(String text, String errorMessage) throws TopazException {
        try {
            return LocalDateTime.parse(text, DATE_TIME_INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            try {
                return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            } catch (DateTimeParseException ignoredException) {
                try {
                    return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (DateTimeParseException ignoredAgainException) {
                    throw new TopazException(errorMessage);
                }
            }
        }
    }

    /** Returns whether a date string includes a time component. */
    private static boolean hasTimeComponent(String text) {
        return !text.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private static int parseTaskNumber(String command, String action, int taskCount)
            throws TopazException {
        String numberText = command.substring(action.length()).trim();
        if (numberText.isEmpty()) {
            throw new TopazException("Please provide a task number after " + action + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new TopazException("The task number must be an integer.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new TopazException("That task number is not in your list.");
        }
        return taskNumber - 1;
    }

    private static String requireText(String text, String message) throws TopazException {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new TopazException(message);
        }
        if (trimmedText.contains("|")) {
            throw new TopazException("Task details cannot contain the | character.");
        }
        return trimmedText;
    }

    /** Adds a task and restores the list if saving fails. */
    private static void addAndSaveTask(TaskList tasks, Task task) throws TopazException {
        tasks.add(task);
        try {
            saveTasks(tasks.asList());
        } catch (TopazException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
    }

    /** Changes a task's completion state and restores it if saving fails. */
    private static void updateTaskStatus(TaskList tasks, int taskIndex, boolean isDone)
            throws TopazException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        if (isDone) {
            tasks.markAsDone(taskIndex);
        } else {
            tasks.markAsNotDone(taskIndex);
        }
        try {
            saveTasks(tasks.asList());
        } catch (TopazException exception) {
            if (wasDone) {
                tasks.markAsDone(taskIndex);
            } else {
                tasks.markAsNotDone(taskIndex);
            }
            throw exception;
        }
    }

    /** Removes a task and restores it to its original position if saving fails. */
    private static Task deleteAndSaveTask(TaskList tasks, int taskIndex) throws TopazException {
        Task task = tasks.remove(taskIndex);
        try {
            saveTasks(tasks.asList());
            return task;
        } catch (TopazException exception) {
            tasks.add(taskIndex, task);
            throw exception;
        }
    }

    /** Runs the chatbot until the user enters the bye command. */
    public void run() {
        TaskList tasks;
        try {
            tasks = new TaskList(loadTasks());
        } catch (TopazException exception) {
            ui.showLoadingError(exception);
            return;
        }

        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            try {
                if (command.equals("bye")) {
                    ui.showGoodbye();
                    break;
                }

                if (command.equals("list")) {
                    ui.showTaskList(tasks);
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    int taskIndex = parseTaskNumber(command, "mark", tasks.size());
                    updateTaskStatus(tasks, taskIndex, true);
                    ui.showMarkedTask(tasks.get(taskIndex));
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int taskIndex = parseTaskNumber(command, "unmark", tasks.size());
                    updateTaskStatus(tasks, taskIndex, false);
                    ui.showUnmarkedTask(tasks.get(taskIndex));
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    int taskIndex = parseTaskNumber(command, "delete", tasks.size());
                    Task task = deleteAndSaveTask(tasks, taskIndex);
                    ui.showDeletedTask(task, tasks.size());
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = requireText(command.substring(4),
                            "The description of a todo cannot be empty.");
                    Task task = new Todo(description);
                    addAndSaveTask(tasks, task);
                    ui.showAddedTask(task, tasks.size());
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    String content = command.substring(8).trim();
                    int byIndex = content.indexOf(" /by ");
                    if (byIndex < 0) {
                        if (content.endsWith(" /by")) {
                            throw new TopazException("The deadline time cannot be empty.");
                        }
                        throw new TopazException("Use: deadline <description> /by <time>.");
                    }
                    String description = requireText(content.substring(0, byIndex),
                            "The description of a deadline cannot be empty.");
                    String by = requireText(content.substring(byIndex + 5),
                            "The deadline time cannot be empty.");
                    LocalDateTime byDateTime = parseDateTime(by,
                            "Use a date as yyyy-MM-dd or d/M/yyyy HHmm.");
                    Task task = new Deadline(description, byDateTime, hasTimeComponent(by));
                    addAndSaveTask(tasks, task);
                    ui.showAddedTask(task, tasks.size());
                } else if (command.equals("event") || command.startsWith("event ")) {
                    String content = command.substring(5).trim();
                    int fromIndex = content.indexOf(" /from ");
                    int toIndex = content.indexOf(" /to ");
                    if (fromIndex < 0 || toIndex < 0 || fromIndex > toIndex
                            || content.indexOf(" /from ", fromIndex + 1) >= 0
                            || content.indexOf(" /to ", toIndex + 1) >= 0) {
                        if (content.endsWith(" /to")) {
                            throw new TopazException("The event end time cannot be empty.");
                        }
                        throw new TopazException("Use: event <description> /from <time> /to <time>.");
                    }
                    if (toIndex < fromIndex + 7) {
                        throw new TopazException("The event start time cannot be empty.");
                    }
                    String description = requireText(content.substring(0, fromIndex),
                            "The description of an event cannot be empty.");
                    String from = requireText(content.substring(fromIndex + 7, toIndex),
                            "The event start time cannot be empty.");
                    String to = requireText(content.substring(toIndex + 5),
                            "The event end time cannot be empty.");
                    LocalDateTime fromDateTime = parseDateTime(from,
                            "Use a date as yyyy-MM-dd or d/M/yyyy HHmm.");
                    LocalDateTime toDateTime = parseDateTime(to,
                            "Use a date as yyyy-MM-dd or d/M/yyyy HHmm.");
                    Task task = new Event(description, fromDateTime, toDateTime,
                            hasTimeComponent(from), hasTimeComponent(to));
                    addAndSaveTask(tasks, task);
                    ui.showAddedTask(task, tasks.size());
                } else {
                    throw new TopazException("I'm sorry, but I don't know what that means.");
                }
            } catch (TopazException exception) {
                ui.showError(exception);
            }

            ui.showSeparator();
        }
    }

    /** Starts the Topaz chatbot. */
    public static void main(String[] args) {
        new Topaz().run();
    }
}
