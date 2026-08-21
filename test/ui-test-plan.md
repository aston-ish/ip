# UI Test Plan

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

### Test case: Interleaved task creation and invalid commands

Aim: Verify that valid tasks are stored while invalid task commands do not change the list.

Input:
```text
todo read book
todo
list
deadline return book /by Sunday
deadline return book
list
event project meeting /from Mon 2pm /to 4pm
event project meeting /from Mon 2pm
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
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Use: deadline <description> /by <time>.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Use: event <description> /from <time> /to <time>.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Sunday)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
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
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 The task number must be an integer.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 That task number is not in your list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 I'm sorry, but I don't know what that means.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
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
deadline second /by tomorrow
event third /from 2pm /to 4pm
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
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] second (by: tomorrow)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] third (from: 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] second (by: tomorrow)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] first
 2.[E][ ] third (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Please provide a task number after delete.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] first
 2.[E][ ] third (from: 2pm to: 4pm)
____________________________________________________________
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
____________________________________________________________
 Got it. I've added this task:
   [T][ ] alpha
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 That task number is not in your list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] alpha
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] beta
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 The task number must be an integer.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] alpha
 2.[T][ ] beta
____________________________________________________________
____________________________________________________________
 I'm sorry, but I don't know what that means.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] alpha
 2.[T][ ] beta
____________________________________________________________
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
____________________________________________________________
 Use: event <description> /from <time> /to <time>.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
