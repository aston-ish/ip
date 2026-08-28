/** Represents an operation requested by the user. */
public abstract class Command {
    /** Executes this command using the application's current collaborators. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws TopazException;

    /** Returns whether this command ends the application. */
    public boolean isExit() {
        return false;
    }
}
