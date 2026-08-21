import java.util.Scanner;

public class Topaz {
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

        Task[] tasks = new Task[100];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
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
                int taskNumber = Integer.parseInt(command.substring(5));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + tasks[taskIndex].getDisplayIcon() + " "
                        + tasks[taskIndex].getDescription());
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[taskIndex].getDisplayIcon() + " "
                        + tasks[taskIndex].getDescription());
            } else if (command.startsWith("todo ")) {
                String description = command.substring(5);
                tasks[taskCount] = new Todo(description);
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + tasks[taskCount - 1].getDisplayIcon() + " "
                        + tasks[taskCount - 1].getDescription());
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
            } else if (command.startsWith("deadline ")) {
                String[] parts = command.substring(9).split(" /by ", 2);
                Task task = new Deadline(parts[0], parts[1]);
                tasks[taskCount] = task;
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
            } else if (command.startsWith("event ")) {
                String[] parts = command.substring(6).split(" /from | /to ", 3);
                Task task = new Event(parts[0], parts[1], parts[2]);
                tasks[taskCount] = task;
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + task.getDisplayIcon() + " " + task.getDescription());
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
            } else if (taskCount < tasks.length) {
                tasks[taskCount] = new Todo(command);
                taskCount++;
                System.out.println(" added: " + command);
            }

            System.out.println(separator);
        }
    }
}
