package topaz.command;

import topaz.TopazException;
import topaz.storage.Storage;
import topaz.task.TaskList;
import topaz.ui.Ui;

/** Finds and displays tasks whose descriptions contain a keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a command that searches for the given keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TopazException {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
