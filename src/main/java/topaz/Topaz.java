package topaz;

import java.nio.file.Path;
import java.nio.file.Paths;

import topaz.command.Command;
import topaz.parser.Parser;
import topaz.storage.Storage;
import topaz.task.TaskList;
import topaz.ui.Ui;

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
                Command parsedCommand = parser.parse(command, tasks.size());
                parsedCommand.execute(tasks, ui, storage);
                if (parsedCommand.isExit()) {
                    break;
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
