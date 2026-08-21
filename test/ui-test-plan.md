# UI Test Plan

Console-output test cases for `Ace` (`src/main/java/Ace.java`), run by the `test-ui` skill.

Each test case starts the program fresh with an empty task list, feeds it a fixed sequence of
commands as stdin, and records the exact stdout the program should produce. Comparison is exact
string equality — spacing inside `[T][ ]`-style status brackets and the `____...` separator lines
are part of the spec, not incidental formatting.

## Format

```markdown
### Test Case N: <short name>

**Aim:** <what this case checks, and why>

**Inputs:**
\```
<command 1>
<command 2>
...
bye
\```

**Expected Output:**
\```
<exact full stdout for the session above>
\```
```

## Note on the two cases below

These two cases encode the **target** output format (as agreed on with the user, e.g. `Got it.
I've added this task:` / `Now you have N tasks in the list.` / no space between `[T]` and `[ ]`)
— not necessarily what the current implementation prints today. If `test-ui` reports a FAIL here,
that's expected until the known formatting bugs in `Todo`/`Deadline`/`Event`/`TaskManager` (extra
space between the type and status brackets, list-numbering spacing, message wording) are fixed.
Once fixed, both cases should pass; add further cases as new commands/behavior are implemented.

### Test Case 1: Add a todo task and list it

**Aim:** Verify that `todo` adds a task with the correct confirmation message/format, and that `list` displays it correctly.

**Inputs:**
```
todo borrow book
list
bye
```

**Expected Output:**
```
Hello from
   ___   _____  _____
  / _ \ /  __ \|  ___|
 / /_\ \| /  \| |__  
 |  _  || |    |  __| 
 | | | || \__/\| |___ 
 \_| |_/ \____/\____/

____________________________________________________________
 Hello! I'm ACE, your personal task manager!
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case 2: Add a deadline and an event, then list both

**Aim:** Verify that `deadline` and `event` parse their `/by`, `/from`, `/to` arguments correctly and format the date/time details as expected, and that `list` displays multiple task types correctly.

**Inputs:**
```
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected Output:**
```
Hello from
   ___   _____  _____
  / _ \ /  __ \|  ___|
 / /_\ \| /  \| |__  
 |  _  || |    |  __| 
 | | | || \__/\| |___ 
 \_| |_/ \____/\____/

____________________________________________________________
 Hello! I'm ACE, your personal task manager!
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Sunday)
 2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
