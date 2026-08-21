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

        String[] tasks = new String[100];
        boolean[] completed = new boolean[100];
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
                    String status = completed[i] ? "[X]" : "[ ]";
                    System.out.println(" " + (i + 1) + "." + status + " " + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                int taskIndex = taskNumber - 1;
                completed[taskIndex] = true;
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   [X] " + tasks[taskIndex]);
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                int taskIndex = taskNumber - 1;
                completed[taskIndex] = false;
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   [ ] " + tasks[taskIndex]);
            } else if (taskCount < tasks.length) {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println(" added: " + command);
            }

            System.out.println(separator);
        }
    }
}
