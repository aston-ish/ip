package topaz.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the task collection operations provided by {@link TaskList}. */
class TaskListTest {
    @Test
    void newTaskList_emptyList_hasZeroTasks() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
    }

    @Test
    void add_tasks_areStoredInInsertionOrder() {
        TaskList taskList = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");

        taskList.add(first);
        taskList.add(second);

        assertEquals(2, taskList.size());
        assertEquals(first, taskList.get(0));
        assertEquals(second, taskList.get(1));
    }

    @Test
    void addAtIndex_task_isInsertedAtRequestedPosition() {
        TaskList taskList = new TaskList();
        Task first = new Todo("first");
        Task inserted = new Todo("inserted");
        Task last = new Todo("last");
        taskList.add(first);
        taskList.add(last);

        taskList.add(1, inserted);

        assertEquals(List.of(first, inserted, last), taskList.asList());
    }

    @Test
    void remove_task_isReturnedAndLaterTasksShiftLeft() {
        TaskList taskList = new TaskList(List.of(
                new Todo("first"), new Todo("second"), new Todo("last")));

        Task removed = taskList.remove(1);

        assertEquals("second", removed.getDescription());
        assertEquals(2, taskList.size());
        assertEquals("last", taskList.get(1).getDescription());
    }

    @Test
    void markAndUnmark_taskStatusIsReversible() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        taskList.markAsDone(0);
        assertTrue(taskList.get(0).isDone());

        taskList.markAsNotDone(0);
        assertFalse(taskList.get(0).isDone());
    }

    @Test
    void constructor_copiesInputList_subsequentInputChangesDoNotAffectTaskList() {
        List<Task> source = new ArrayList<>();
        source.add(new Todo("original"));
        TaskList taskList = new TaskList(source);

        source.clear();

        assertEquals(1, taskList.size());
        assertEquals("original", taskList.get(0).getDescription());
    }

    @Test
    void asList_returnedViewCannotModifyTaskList() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        assertThrows(UnsupportedOperationException.class,
                () -> taskList.asList().add(new Todo("another")));
        assertEquals(1, taskList.size());
    }
}
