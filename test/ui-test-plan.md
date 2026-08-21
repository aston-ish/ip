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

## Pending UI coverage

The inherited task-type feature requires executable UI cases for:

- adding a todo with `todo borrow book`;
- adding a deadline with `deadline return book /by Sunday`;
- adding an event with `event project meeting /from Mon 2pm /to 4pm`; and
- listing all three task types with their type and completion icons.

These cases are pending until a Java 25 runtime is available to capture the exact
full console output, including the startup banner and separators.
