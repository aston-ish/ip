import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.PrintWriter;
import java.util.Scanner;

public class Topaz {
    private static final File SAVE_FILE = new File(
            System.getProperty("topaz.dataFile", "./data/Topaz.txt"));

    /**
     * Saves every task in the current list to the hard disk.
     *
     * @param tasks the tasks to save
     * @throws TopazException if the save file cannot be created or written
     */
    private static void saveTasks(List<Task> tasks) throws TopazException {
        try {
            File parentDirectory = SAVE_FILE.getParentFile();
            if (parentDirectory != null && parentDirectory.exists() && !parentDirectory.isDirectory()) {
                throw new TopazException("The data directory path is not a directory.");
            }
            if (parentDirectory != null && !parentDirectory.exists() && !parentDirectory.mkdirs()) {
                throw new TopazException("Unable to create the data directory.");
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE, StandardCharsets.UTF_8))) {
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
            if (!SAVE_FILE.exists()) {
                return tasks;
            }
            if (!SAVE_FILE.isFile()) {
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
            task = new Deadline(values[2], values[3]);
        } else if (values.length == 5 && values[0].equals("E")) {
            task = new Event(values[2], values[3], values[4]);
        } else {
            throw new TopazException("Unable to load a saved task.");
        }

        if (values[1].equals("1")) {
            task.markAsDone();
        }
        return task;
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
    private static void addAndSaveTask(List<Task> tasks, Task task) throws TopazException {
        tasks.add(task);
        try {
            saveTasks(tasks);
        } catch (TopazException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
    }

    /** Changes a task's completion state and restores it if saving fails. */
    private static void updateTaskStatus(List<Task> tasks, int taskIndex, boolean isDone)
            throws TopazException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        try {
            saveTasks(tasks);
        } catch (TopazException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw exception;
        }
    }

    /** Removes a task and restores it to its original position if saving fails. */
    private static Task deleteAndSaveTask(List<Task> tasks, int taskIndex) throws TopazException {
        Task task = tasks.remove(taskIndex);
        try {
            saveTasks(tasks);
            return task;
        } catch (TopazException exception) {
            tasks.add(taskIndex, task);
            throw exception;
        }
    }

    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        String banner = " _____                 _          \n"
                + "|_   _|__  _ __   __ _| |__       \n"
                + "  | |/ _ \\| '_ \\ / _` | '_ \\      \n"
                + "  | | (_) | |_) | (_| | | | |     \n"
                + "  |_|\\___/| .__/ \\__,_|_| |_|     \n"
                + "           |_|                      \n";

        List<Task> tasks;
        try {
            tasks = loadTasks();
        } catch (TopazException exception) {
            System.out.println(" " + exception.getMessage());
            return;
        }

        System.out.println(separator);
        System.out.println(banner);
        System.out.println("Hello! I'm Topaz.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            try {
                if (command.equals("bye")) {
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(separator);
                    break;
                }

                if (command.equals("list")) {
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i).getDisplayIcon() + " "
                                + tasks.get(i).getDescription());
                    }
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    int taskIndex = parseTaskNumber(command, "mark", tasks.size());
                    updateTaskStatus(tasks, taskIndex, true);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(taskIndex).getDisplayIcon() + " "
                            + tasks.get(taskIndex).getDescription());
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int taskIndex = parseTaskNumber(command, "unmark", tasks.size());
                    updateTaskStatus(tasks, taskIndex, false);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(taskIndex).getDisplayIcon() + " "
                            + tasks.get(taskIndex).getDescription());
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    int taskIndex = parseTaskNumber(command, "delete", tasks.size());
                    Task task = deleteAndSaveTask(tasks, taskIndex);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = requireText(command.substring(4),
                            "The description of a todo cannot be empty.");
                    Task task = new Todo(description);
                    addAndSaveTask(tasks, task);
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
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
                    Task task = new Deadline(description, by);
                    addAndSaveTask(tasks, task);
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
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
                    Task task = new Event(description, from, to);
                    addAndSaveTask(tasks, task);
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                } else {
                    throw new TopazException("I'm sorry, but I don't know what that means.");
                }
            } catch (TopazException exception) {
                System.out.println(" " + exception.getMessage());
            }

            System.out.println(separator);
        }
    }
}
