package topaz.command;

import topaz.TopazException;
import topaz.storage.Storage;
import topaz.task.Task;
import topaz.task.TaskList;
import topaz.ui.Ui;

/**
 * Adds one task to the list and saves the updated task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TopazException {
        tasks.add(task);
        try {
            storage.save(tasks.asList());
        } catch (TopazException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showAddedTask(task, tasks.size());
    }
}
