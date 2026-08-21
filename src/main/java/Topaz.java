import java.util.Scanner;

public class Topaz {
    private static final int MAX_TASKS = 100;

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
        return trimmedText;
    }

    private static void ensureCapacity(int taskCount) throws TopazException {
        if (taskCount >= MAX_TASKS) {
            throw new TopazException("Your task list is full.");
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

        System.out.println(separator);
        System.out.println(banner);
        System.out.println("Hello! I'm Topaz.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            try {
                if (command.equals("bye")) {
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(separator);
                    break;
                }

                if (command.equals("list")) {
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + "." + tasks[i].getDisplayIcon() + " "
                                + tasks[i].getDescription());
                    }
                } else if (command.startsWith("mark ")) {
                    int taskIndex = parseTaskNumber(command, "mark", taskCount);
                    tasks[taskIndex].markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[taskIndex].getDisplayIcon() + " "
                            + tasks[taskIndex].getDescription());
                } else if (command.startsWith("unmark ")) {
                    int taskIndex = parseTaskNumber(command, "unmark", taskCount);
                    tasks[taskIndex].markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[taskIndex].getDisplayIcon() + " "
                            + tasks[taskIndex].getDescription());
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    ensureCapacity(taskCount);
                    String description = requireText(command.substring(4),
                            "The description of a todo cannot be empty.");
                    tasks[taskCount] = new Todo(description);
                    taskCount++;
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + tasks[taskCount - 1].getDisplayIcon() + " "
                            + tasks[taskCount - 1].getDescription());
                    System.out.println(" Now you have " + taskCount + " tasks in the list.");
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    ensureCapacity(taskCount);
                    String content = command.substring(8).trim();
                    int byIndex = content.indexOf(" /by ");
                    if (byIndex < 0) {
                        throw new TopazException("Use: deadline <description> /by <time>.");
                    }
                    String description = requireText(content.substring(0, byIndex),
                            "The description of a deadline cannot be empty.");
                    String by = requireText(content.substring(byIndex + 5),
                            "The deadline time cannot be empty.");
                    Task task = new Deadline(description, by);
                    tasks[taskCount] = task;
                    taskCount++;
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
                    System.out.println(" Now you have " + taskCount + " tasks in the list.");
                } else if (command.equals("event") || command.startsWith("event ")) {
                    ensureCapacity(taskCount);
                    String content = command.substring(5).trim();
                    int fromIndex = content.indexOf(" /from ");
                    int toIndex = content.indexOf(" /to ");
                    if (fromIndex < 0 || toIndex < 0 || fromIndex > toIndex) {
                        throw new TopazException("Use: event <description> /from <time> /to <time>.");
                    }
                    String description = requireText(content.substring(0, fromIndex),
                            "The description of an event cannot be empty.");
                    String from = requireText(content.substring(fromIndex + 7, toIndex),
                            "The event start time cannot be empty.");
                    String to = requireText(content.substring(toIndex + 5),
                            "The event end time cannot be empty.");
                    Task task = new Event(description, from, to);
                    tasks[taskCount] = task;
                    taskCount++;
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
                    System.out.println(" Now you have " + taskCount + " tasks in the list.");
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
