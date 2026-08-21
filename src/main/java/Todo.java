/** Represents a basic todo task. */
public class Todo extends Task {
    /** Creates an incomplete todo task. */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }
}
