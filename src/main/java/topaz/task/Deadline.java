package topaz.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a specified time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HHmm", Locale.ENGLISH);
    private final LocalDateTime by;
    private final boolean hasTime;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        this.by = by;
        this.hasTime = hasTime;
    }

    /** Returns the deadline task type. */
    @Override
    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    /** Returns the description together with the formatted deadline. */
    @Override
    public String getDescription() {
        String formattedBy = hasTime ? by.format(DISPLAY_FORMAT) : by.toLocalDate().format(
                DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH));
        return super.getDescription() + " (by: " + formattedBy + ")";
    }

    /** Serializes this deadline for storage. */
    @Override
    public String toFileString() {
        String savedBy = hasTime ? by.toString() : by.toLocalDate().toString();
        return "D | " + (isDone() ? "1" : "0") + " | " + super.getDescription() + " | " + savedBy;
    }
}
