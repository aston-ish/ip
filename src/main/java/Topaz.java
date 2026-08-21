import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Topaz {
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

        List<Task> tasks = new ArrayList<>();
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
                    tasks.get(taskIndex).markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(taskIndex).getDisplayIcon() + " "
                            + tasks.get(taskIndex).getDescription());
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int taskIndex = parseTaskNumber(command, "unmark", tasks.size());
                    tasks.get(taskIndex).markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(taskIndex).getDisplayIcon() + " "
                            + tasks.get(taskIndex).getDescription());
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    int taskIndex = parseTaskNumber(command, "delete", tasks.size());
                    Task task = tasks.get(taskIndex);
                    tasks.remove(taskIndex);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = requireText(command.substring(4),
                            "The description of a todo cannot be empty.");
                    Task task = new Todo(description);
                    tasks.add(task);
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
                    tasks.add(task);
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
                    tasks.add(task);
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
