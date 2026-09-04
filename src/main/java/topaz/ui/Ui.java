package topaz.ui;

import java.util.List;
import java.util.Scanner;

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

    /**
     * Creates a UI that reads commands from the standard input stream.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from the user.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the application greeting.
     */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Topaz.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }

    /**
     * Shows an error that prevented Topaz from starting.
     *
     * @param exception the startup error to display
     */
    public void showLoadingError(TopazException exception) {
        System.out.println(" " + exception.getMessage());
    }

    /**
     * Shows every task in the task list.
     *
     * @param tasks the task list to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i).getDisplayIcon() + " "
                    + tasks.get(i).getDescription());
        }
    }

    /**
     * Shows tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks the matching tasks to display
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println(" Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            Task task = matchingTasks.get(i);
            System.out.println(" " + (i + 1) + "." + task.getDisplayIcon() + " "
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
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows a task marked as done.
     *
     * @param task the task that was marked as done
     */
    public void showMarkedTask(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
    }

    /**
     * Shows a task marked as not done.
     *
     * @param task the task that was marked as not done
     */
    public void showUnmarkedTask(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
    }

    /**
     * Shows a task removed from the list.
     *
     * @param task the task that was removed
     * @param taskCount the number of tasks remaining after removal
     */
    public void showDeletedTask(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows an error caused by a command.
     *
     * @param exception the command error to display
     */
    public void showError(TopazException exception) {
        System.out.println(" " + exception.getMessage());
    }

    /**
     * Shows the standard separator below a response.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Shows the goodbye message and closing separator.
     */
    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
        showSeparator();
    }
}
