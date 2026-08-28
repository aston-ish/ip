/**
 * Represents a task entered by the user.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a completed task, otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return true if this task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /** Returns this task's type. */
    public abstract TaskType getTaskType();

    /**
     * Returns this task in the format used by the save file.
     *
     * @return a line representing this task
     */
    public abstract String toFileString();

    /**
     * Returns the task type and completion status for display.
     *
     * @return a display icon such as {@code [D][ ]}
     */
    public String getDisplayIcon() {
        return "[" + getTaskType().getIcon() + "][" + getStatusIcon() + "]";
    }
}
