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
