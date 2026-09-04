package topaz.command;

import topaz.TopazException;
import topaz.storage.Storage;
import topaz.task.Task;
import topaz.task.TaskList;
import topaz.ui.Ui;

/**
 * Marks one task as not done and saves the updated task list.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command for the given zero-based task index.
     *
     * @param taskIndex the zero-based index of the task to unmark
     */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TopazException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        tasks.markAsNotDone(taskIndex);
        try {
            storage.save(tasks.asList());
        } catch (TopazException exception) {
            if (wasDone) {
                tasks.markAsDone(taskIndex);
            } else {
                tasks.markAsNotDone(taskIndex);
            }
            throw exception;
        }
        ui.showUnmarkedTask(task);
    }
}
