import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task with a start and end time. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HHmm", Locale.ENGLISH);
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean hasFromTime;
    private final boolean hasToTime;

    /** Creates an incomplete event task. */
    public Event(String description, LocalDateTime from, LocalDateTime to,
                 boolean hasFromTime, boolean hasToTime) {
        super(description);
        this.from = from;
        this.to = to;
        this.hasFromTime = hasFromTime;
        this.hasToTime = hasToTime;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    @Override
    public String getDescription() {
        String formattedFrom = hasFromTime ? from.format(DISPLAY_FORMAT) : from.toLocalDate().format(
                DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH));
        String formattedTo = hasToTime ? to.format(DISPLAY_FORMAT) : to.toLocalDate().format(
                DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH));
        return super.getDescription() + " (from: " + formattedFrom + " to: " + formattedTo + ")";
    }

    @Override
    public String toFileString() {
        String savedFrom = hasFromTime ? from.toString() : from.toLocalDate().toString();
        String savedTo = hasToTime ? to.toString() : to.toLocalDate().toString();
        return "E | " + (isDone() ? "1" : "0") + " | " + super.getDescription()
                + " | " + savedFrom + " | " + savedTo;
    }
}
