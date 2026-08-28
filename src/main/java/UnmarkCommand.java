/** Marks one task as not done and saves the updated task list. */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /** Creates a command for the given zero-based task index. */
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
