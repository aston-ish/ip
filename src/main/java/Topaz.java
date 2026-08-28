import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class Topaz {
    private static final Path DEFAULT_SAVE_FILE = Paths.get("data", "Topaz.txt");
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private final Ui ui;
    private final Storage storage;

    /** Creates Topaz with a console user interface. */
    public Topaz() {
        ui = new Ui();
        Path saveFile = Paths.get(System.getProperty("topaz.dataFile", DEFAULT_SAVE_FILE.toString()));
        storage = new Storage(saveFile);
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
    private void addAndSaveTask(TaskList tasks, Task task) throws TopazException {
        tasks.add(task);
        try {
            storage.save(tasks.asList());
        } catch (TopazException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
    }

    /** Changes a task's completion state and restores it if saving fails. */
    private void updateTaskStatus(TaskList tasks, int taskIndex, boolean isDone)
            throws TopazException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        if (isDone) {
            tasks.markAsDone(taskIndex);
        } else {
            tasks.markAsNotDone(taskIndex);
        }
        try {
            storage.save(tasks.asList());
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
    private Task deleteAndSaveTask(TaskList tasks, int taskIndex) throws TopazException {
        Task task = tasks.remove(taskIndex);
        try {
            storage.save(tasks.asList());
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
            tasks = new TaskList(storage.load());
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
