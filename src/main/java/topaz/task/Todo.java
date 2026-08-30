package topaz.task;

/** Represents a basic todo task. */
public class Todo extends Task {
    /** Creates an incomplete todo task. */
    public Todo(String description) {
        super(description);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.TODO;
    }

    @Override
    public String toFileString() {
        return "T | " + (isDone() ? "1" : "0") + " | " + getDescription();
    }
}
