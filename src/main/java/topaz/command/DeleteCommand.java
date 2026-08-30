package topaz.command;

import topaz.TopazException;
import topaz.storage.Storage;
import topaz.task.Task;
import topaz.task.TaskList;
import topaz.ui.Ui;

/** Removes one task from the list and saves the updated task list. */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /** Creates a command for the given zero-based task index. */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TopazException {
        Task task = tasks.remove(taskIndex);
        try {
            storage.save(tasks.asList());
        } catch (TopazException exception) {
            tasks.add(taskIndex, task);
            throw exception;
        }
        ui.showDeletedTask(task, tasks.size());
    }
}
