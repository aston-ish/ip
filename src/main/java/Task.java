/**
 * Represents a task entered by the user.
 */
public class Task {
    protected String description;
    protected String type;
    protected String by;
    protected String from;
    protected String to;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        this(description, "T");
    }

    /**
     * Creates an incomplete task of the given type.
     *
     * @param description the task description
     * @param type the task type icon, such as {@code T}, {@code D}, or {@code E}
     */
    public Task(String description, String type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /** Sets the deadline text for this task. */
    public void setBy(String by) {
        this.by = by;
    }

    /** Sets the start and end time text for this task. */
    public void setTime(String from, String to) {
        this.from = from;
        this.to = to;
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
        if (type.equals("D")) {
            return description + " (by: " + by + ")";
        }
        if (type.equals("E")) {
            return description + " (from: " + from + " to: " + to + ")";
        }
        return description;
    }

    /**
     * Returns the task type and completion status for display.
     *
     * @return a display icon such as {@code [D][ ]}
     */
    public String getDisplayIcon() {
        return "[" + type + "][" + getStatusIcon() + "]";
    }
}
