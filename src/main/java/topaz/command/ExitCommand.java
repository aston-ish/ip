package topaz.command;

import topaz.storage.Storage;
import topaz.task.TaskList;
import topaz.ui.Ui;

/** Ends the chatbot session. */
public class ExitCommand extends Command {
    /** Displays the goodbye message. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** Returns true because this command terminates the session. */
    @Override
    public boolean isExit() {
        return true;
    }
}
