package topaz.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import topaz.TopazException;
import topaz.task.Task;
import topaz.task.TaskList;

/**
 * Handles console input and all messages shown to the user.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BANNER = " _____                 _          \n"
            + "|_   _|__  _ __   __ _| |__       \n"
            + "  | |/ _ \\| '_ \\ / _` | '_ \\      \n"
            + "  | | (_) | |_) | (_| | | | |     \n"
            + "  |_|\\___/| .__/ \\__,_|_| |_|     \n"
            + "           |_|                      \n";

    private final Scanner scanner;
    private final Consumer<String> output;

    /**
     * Creates a UI that reads commands from the standard input stream.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::println;
    }

    /**
     * Creates a UI that appends messages to the given response.
     *
     * @param response the response that receives UI messages
     */
    public Ui(StringBuilder response) {
        scanner = null;
        output = message -> response.append(message).append(System.lineSeparator());
    }

    /**
     * Returns whether another command is available from the user.
     */
    public boolean hasNextCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This UI does not read console commands.");
        }
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from the user.
     */
    public String readCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This UI does not read console commands.");
        }
        return scanner.nextLine();
    }

    /**
     * Shows the application greeting.
     */
    public void showWelcome() {
        output.accept(SEPARATOR);
        output.accept(BANNER);
        output.accept("Hello! I'm Topaz.");
        output.accept("What can I do for you?");
        output.accept(SEPARATOR);
    }

    /**
     * Shows an error that prevented Topaz from starting.
     *
     * @param exception the startup error to display
     */
    public void showLoadingError(TopazException exception) {
        output.accept(" " + exception.getMessage());
    }

    /**
     * Shows every task in the task list.
     *
     * @param tasks the task list to display
     */
    public void showTaskList(TaskList tasks) {
        output.accept(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.accept(" " + (i + 1) + "." + tasks.get(i).getDisplayIcon() + " "
                    + tasks.get(i).getDescription());
        }
    }

    /**
     * Shows tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks the matching tasks to display
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        output.accept(" Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            Task task = matchingTasks.get(i);
            output.accept(" " + (i + 1) + "." + task.getDisplayIcon() + " "
                    + task.getDescription());
        }
    }

    /**
     * Shows a newly added task.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks after adding the task
     */
    public void showAddedTask(Task task, int taskCount) {
        output.accept(" Got it. I've added this task:");
        output.accept("   " + task.getDisplayIcon() + " " + task.getDescription());
        output.accept(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows a task marked as done.
     *
     * @param task the task that was marked as done
     */
    public void showMarkedTask(Task task) {
        output.accept(" Nice! I've marked this task as done:");
        output.accept("   " + task.getDisplayIcon() + " " + task.getDescription());
    }

    /**
     * Shows a task marked as not done.
     *
     * @param task the task that was marked as not done
     */
    public void showUnmarkedTask(Task task) {
        output.accept(" OK, I've marked this task as not done yet:");
        output.accept("   " + task.getDisplayIcon() + " " + task.getDescription());
    }

    /**
     * Shows a task removed from the list.
     *
     * @param task the task that was removed
     * @param taskCount the number of tasks remaining after removal
     */
    public void showDeletedTask(Task task, int taskCount) {
        output.accept(" Noted. I've removed this task:");
        output.accept("   " + task.getDisplayIcon() + " " + task.getDescription());
        output.accept(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows an error caused by a command.
     *
     * @param exception the command error to display
     */
    public void showError(TopazException exception) {
        output.accept(" " + exception.getMessage());
    }

    /**
     * Shows the standard separator below a response.
     */
    public void showSeparator() {
        output.accept(SEPARATOR);
    }

    /**
     * Shows the goodbye message and closing separator.
     */
    public void showGoodbye() {
        output.accept(" Bye. Hope to see you again soon!");
        showSeparator();
    }
}
