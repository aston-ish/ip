# UI Test Plan

The tutorial change launches the JavaFX graphical interface by default rather than
the command-line chatbot. The existing command-line test cases remain applicable
when `topaz.Topaz` is run directly. This plan cannot automate the current GUI
iteration; manually verify that submitting text with Enter or Send adds a
right-aligned user dialog and a left-aligned response dialog. Verify that `todo`,
`deadline`, `event`, `list`, `find`, `mark`, `unmark`, and `delete` produce the
same responses and save task changes as their command-line equivalents.

Record every requested command-line UI test case here before running the test session.
The expected output must match stdout exactly, including prompts, separators, spaces,
and line breaks.

## Test case format

```markdown
### Test case: <name>

Aim: <what this verifies>

Input:
```text
<commands, one per line>
```

Expected output:
```text
<exact stdout>
```
```

The `test-ui` skill runs each case in a fresh process and stops immediately at the
first failure, showing the actual and expected output.

Automated UI tests use a temporary save-file path so that they do not overwrite a
user's `data/Topaz.txt` file.

## Persistence integration check

Aim: Verify that a fresh chatbot process loads saved tasks with their type, completion status, and time fields intact.

Setup: Create `data/Topaz.txt` with the following contents before starting Topaz:

```text
D | 1 | return book | 2026-12-07
E | 0 | project meeting | 2026-12-08T14:00 | 2026-12-08T16:00
```

Input:

```text
list
bye
```

Expected output after startup:

```text
 Here are the tasks in your list:
 1.[D][X] return book (by: Dec 07 2026)
 2.[E][ ] project meeting (from: Dec 08 2026 1400 to: Dec 08 2026 1600)
```

### Test case: Interleaved task creation and invalid commands

Aim: Verify that valid tasks are stored while invalid task commands do not change the list.

Input:
```text
todo read book
todo
list
deadline return book /by 2026-12-07
deadline return book
list
event project meeting /from 8/12/2026 1400 /to 8/12/2026 1600
event project meeting /from 8/12/2026 1400
list
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
 The description of a todo cannot be empty.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 07 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 Use: deadline <description> /by <time>.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 07 2026)
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 08 2026 1400 to: Dec 08 2026 1600)
 Now you have 3 tasks in the list.
____________________________________________________________
 Use: event <description> /from <time> /to <time>.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 07 2026)
 3.[E][ ] project meeting (from: Dec 08 2026 1400 to: Dec 08 2026 1600)
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Find tasks by keyword

Aim: Verify that find returns matching tasks in list order and rejects a missing keyword.

Input:
```text
todo read book
deadline return book /by 2026-12-07
todo buy milk
find book
find
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 07 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy milk
 Now you have 3 tasks in the list.
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 07 2026)
____________________________________________________________
 Please provide a keyword after find.
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Reserved save-file delimiter

Aim: Verify that task details containing `|` are rejected and do not change the task list, because `|` separates fields in the save file.

Input:
```text
todo read | book
list
deadline return book /by Sun|day
list
event meeting /from 2|pm /to 4pm
list
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 Task details cannot contain the | character.
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
 Task details cannot contain the | character.
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
 Task details cannot contain the | character.
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Save changed task list

Aim: Verify that adding, marking, and deleting tasks saves the remaining task list to `data/Topaz.txt`.

Input:
```text
todo read book
mark 1
deadline return book /by 2026-12-07
event project meeting /from 8/12/2026 1400 /to 8/12/2026 1600
delete 1
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 07 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 08 2026 1400 to: Dec 08 2026 1600)
 Now you have 3 tasks in the list.
____________________________________________________________
 Noted. I've removed this task:
   [T][X] read book
 Now you have 2 tasks in the list.
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

Expected `data/Topaz.txt` after this case:

```text
D | 0 | return book | 2026-12-07
E | 0 | project meeting | 2026-12-08T14:00 | 2026-12-08T16:00
```

### Test case: Interleaved mark, unmark, and invalid task numbers

Aim: Verify that invalid mark and unmark commands do not change completion status.

Input:
```text
todo read book
mark 1
mark zero
list
unmark 1
unmark 2
list
unknown command
list
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
 The task number must be an integer.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
 That task number is not in your list.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
 I'm sorry, but I don't know what that means.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

Both cases intentionally interleave valid and invalid commands so that state changes
caused by rejected inputs are visible in later `list` output.

### Test case: Delete tasks and preserve list order

Aim: Verify that deleting a task removes the correct item, shifts later tasks up, and rejects a missing task number without changing state.

Input:
```text
todo first
deadline second /by 2026-12-09
event third /from 10/12/2026 1400 /to 10/12/2026 1600
delete 2
list
delete
list
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first
 Now you have 1 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [D][ ] second (by: Dec 09 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [E][ ] third (from: Dec 10 2026 1400 to: Dec 10 2026 1600)
 Now you have 3 tasks in the list.
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] second (by: Dec 09 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] first
 2.[E][ ] third (from: Dec 10 2026 1400 to: Dec 10 2026 1600)
____________________________________________________________
 Please provide a task number after delete.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] first
 2.[E][ ] third (from: Dec 10 2026 1400 to: Dec 10 2026 1600)
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Invalid task numbers do not change state

Aim: Verify that out-of-range and non-numeric mark commands do not alter or add tasks.

Input:
```text
todo alpha
mark 2
list
todo beta
unmark abc
list
blah
list
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] alpha
 Now you have 1 tasks in the list.
____________________________________________________________
 That task number is not in your list.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] alpha
____________________________________________________________
 Got it. I've added this task:
   [T][ ] beta
 Now you have 2 tasks in the list.
____________________________________________________________
 The task number must be an integer.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] alpha
 2.[T][ ] beta
____________________________________________________________
 I'm sorry, but I don't know what that means.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] alpha
 2.[T][ ] beta
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Empty event fields are rejected

Aim: Verify that an event with an empty `from` value is rejected and does not corrupt the task list.

Input:
```text
event meeting /from /to 4pm
list
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 The event start time cannot be empty.
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: First run without a save file

Aim: Verify that Topaz starts with an empty task list and creates its save file when no saved data exists.

Input:
```text
list
todo first task
list
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first task
 Now you have 1 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] first task
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Parse and display dates and times

Aim: Verify that valid dates are stored as date/time values, displayed in a readable format, and that invalid dates do not change the list.

Input:
```text
deadline return book /by 2/12/2019 1800
list
deadline review notes /by 2019-10-15
list
deadline invalid date /by 31/02/2019 1800
list
event project meeting /from 15/10/2019 1400 /to 15/10/2019 1600
list
event invalid event /from 15/10/2019 1400 /to 2019-02-29
list
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019 1800)
 Now you have 1 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 02 2019 1800)
____________________________________________________________
 Got it. I've added this task:
   [D][ ] review notes (by: Oct 15 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 02 2019 1800)
 2.[D][ ] review notes (by: Oct 15 2019)
____________________________________________________________
 Use a date as yyyy-MM-dd or d/M/yyyy HHmm.
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 02 2019 1800)
 2.[D][ ] review notes (by: Oct 15 2019)
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)
 Now you have 3 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 02 2019 1800)
 2.[D][ ] review notes (by: Oct 15 2019)
 3.[E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)
____________________________________________________________
 Use a date as yyyy-MM-dd or d/M/yyyy HHmm.
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 02 2019 1800)
 2.[D][ ] review notes (by: Oct 15 2019)
 3.[E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Display date-only event times

Aim: Verify that date-only event values are stored as dates and displayed without an artificial midnight time.

Input:
```text
event conference /from 2019-10-15 /to 2019-10-16
list
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [E][ ] conference (from: Oct 15 2019 to: Oct 16 2019)
 Now you have 1 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] conference (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Test case: Bare numbered commands and empty time fields

Aim: Verify that missing task numbers and empty deadline or event time fields are rejected without changing existing tasks.

Input:
```text
todo read
mark
list
unmark
list
deadline return /by
list
event meeting /from /to 4pm
list
event meeting /from 2pm /to
list
bye
```

Expected output:
```text
____________________________________________________________
 _____                 _          
|_   _|__  _ __   __ _| |__       
  | |/ _ \| '_ \ / _` | '_ \      
  | | (_) | |_) | (_| | | | |     
  |_|\___/| .__/ \__,_|_| |_|     
           |_|                      

Hello! I'm Topaz.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
 Please provide a task number after mark.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
 Please provide a task number after unmark.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
 The deadline time cannot be empty.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
 The event start time cannot be empty.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
 The event end time cannot be empty.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
