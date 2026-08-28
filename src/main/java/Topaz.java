import java.nio.file.Path;
import java.nio.file.Paths;

public class Topaz {
    private static final Path DEFAULT_SAVE_FILE = Paths.get("data", "Topaz.txt");
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;

    /** Creates Topaz with a console user interface. */
    public Topaz() {
        ui = new Ui();
        Path saveFile = Paths.get(System.getProperty("topaz.dataFile", DEFAULT_SAVE_FILE.toString()));
        storage = new Storage(saveFile);
        parser = new Parser();
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
                Command simpleCommand = null;
                if (command.equals("bye")) {
                    simpleCommand = new ExitCommand();
                } else if (command.equals("list")) {
                    simpleCommand = new ListCommand();
                }

                if (simpleCommand != null) {
                    simpleCommand.execute(tasks, ui, storage);
                    if (simpleCommand.isExit()) {
                        break;
                    }
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    Command markCommand = parser.parseMarkCommand(command, tasks.size());
                    markCommand.execute(tasks, ui, storage);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    Command unmarkCommand = parser.parseUnmarkCommand(command, tasks.size());
                    unmarkCommand.execute(tasks, ui, storage);
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    Command deleteCommand = parser.parseDeleteCommand(command, tasks.size());
                    deleteCommand.execute(tasks, ui, storage);
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    Command addCommand = parser.parseTodo(command);
                    addCommand.execute(tasks, ui, storage);
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    Command addCommand = parser.parseDeadline(command);
                    addCommand.execute(tasks, ui, storage);
                } else if (command.equals("event") || command.startsWith("event ")) {
                    Command addCommand = parser.parseEvent(command);
                    addCommand.execute(tasks, ui, storage);
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
