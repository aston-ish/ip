package topaz.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Stores the tasks currently managed by Topaz. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Inserts a task at the given zero-based index. */
    public void add(int taskIndex, Task task) {
        tasks.add(taskIndex, task);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task remove(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /** Marks the task at the given zero-based index as done. */
    public void markAsDone(int taskIndex) {
        tasks.get(taskIndex).markAsDone();
    }

    /** Marks the task at the given zero-based index as not done. */
    public void markAsNotDone(int taskIndex) {
        tasks.get(taskIndex).markAsNotDone();
    }

    /** Returns a read-only view of the tasks for display or storage. */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
