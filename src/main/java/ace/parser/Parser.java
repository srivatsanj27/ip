package ace.parser;

import java.time.LocalDate;

import ace.exception.AceException;
import ace.exception.MissingDescriptionException;
import ace.exception.WrongCommandException;
import ace.exception.WrongTaskNumberException;
import ace.task.Deadline;
import ace.task.Event;
import ace.task.Task;
import ace.task.TaskManager;
import ace.task.Todo;
import ace.ui.Ui;

/**
 * Interprets a single line of raw user input and carries out the corresponding
 * action against the task list, reporting the result through {@link Ui}. This is
 * the one place that knows what each command word means.
 */
public class Parser {
    // Shared opening for every "something's wrong with this command" message,
    // so each throw site below only needs to supply the specific reason and
    // usage hint, not repeat this phrase every time.
    private static final String DISCARD_PREFIX = "Oh no! This card has to be discarded! ";

    /**
     * Parses one line of user input and executes the command it represents.
     *
     * @param input the raw line of user input.
     * @param taskManager the task list to act on.
     * @param ui the UI to report results and errors through.
     * @return true if the command was "bye" and the program should exit, false otherwise.
     * @throws AceException if the input isn't a recognized, well-formed command.
     */
    public static boolean parseCommand(String input, TaskManager taskManager, Ui ui) throws AceException {
        // "list" and "bye" take no argument, so they're checked with plain equality
        // rather than isCommand — isCommand's "starts with X " branch exists
        // specifically to admit a trailing argument, which these commands don't have.
        if (input.equals("list")) {
            taskManager.printTasks();
            return false;
        }
        if (input.equals("bye")) {
            ui.showGoodbye();
            return true;
        }

        if (isCommand(input, "mark")) {
            int taskNumber = parseTaskNumber(extractArgument(input, "mark"), "mark", taskManager);
            taskManager.markTask(taskNumber - 1);
            return false;
        }

        if (isCommand(input, "unmark")) {
            int taskNumber = parseTaskNumber(extractArgument(input, "unmark"), "unmark", taskManager);
            taskManager.unmarkTask(taskNumber - 1);
            return false;
        }

        if (isCommand(input, "todo")) {
            String todoDescription = extractArgument(input, "todo");
            if (todoDescription.isEmpty()) {
                throw new MissingDescriptionException(
                        DISCARD_PREFIX + "A todo needs a description! Try: todo <description>");
            }

            Task newTodo = new Todo(todoDescription);
            taskManager.addTask(newTodo);
            ui.showTaskAdded(newTodo, "To-Do", taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (isCommand(input, "deadline")) {
            String deadlineUsage = "Try: deadline <description> /by <date>";
            String card = extractArgument(input, "deadline");
            if (card.isEmpty()) {
                throw new MissingDescriptionException(
                        DISCARD_PREFIX + "A deadline needs a description! " + deadlineUsage);
            }

            // Bare marker (no required surrounding spaces), so a missing
            // description or date/time before/after it doesn't hide the
            // marker itself — see the note on the event branch below for why.
            String byMarker = "/by";
            int byIndex = card.indexOf(byMarker);
            if (byIndex == -1) {
                throw new WrongCommandException(DISCARD_PREFIX + "A deadline needs '/by'! " + deadlineUsage);
            }

            String description = card.substring(0, byIndex).trim();
            String byWhen = card.substring(byIndex + byMarker.length()).trim();

            if (description.isEmpty()) {
                throw new MissingDescriptionException(
                        DISCARD_PREFIX + "A deadline needs a description! " + deadlineUsage);
            }
            if (byWhen.isEmpty()) {
                throw new WrongCommandException(
                        DISCARD_PREFIX + "Please give a date after '/by'! " + deadlineUsage);
            }

            Task newDeadline = new Deadline(description, byWhen);
            taskManager.addTask(newDeadline);
            ui.showTaskAdded(newDeadline, "Deadline", taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (isCommand(input, "event")) {
            String eventUsage = "Try: event <description> /from <start> /to <end>";
            String card = extractArgument(input, "event");
            if (card.isEmpty()) {
                throw new MissingDescriptionException(
                        DISCARD_PREFIX + "An event needs a description! " + eventUsage);
            }

            // Bare markers (no required surrounding spaces): matching
            // " /from " and " /to " literally meant an empty description,
            // start time, or end time could make the adjacent marker's
            // required space disappear (trimmed away at the edges, or
            // merged into one shared space between two adjacent markers) —
            // the latter previously caused a crash, since the two markers'
            // computed positions could overlap. Explicit emptiness checks
            // below now catch all three cases instead.
            String fromMarker = "/from";
            String toMarker = "/to";
            int fromIndex = card.indexOf(fromMarker);
            int toIndex = card.indexOf(toMarker);
            if (fromIndex == -1 || toIndex == -1 || fromIndex + fromMarker.length() > toIndex) {
                throw new WrongCommandException(
                        DISCARD_PREFIX + "An event needs both '/from' and '/to', with '/from' first! "
                                + eventUsage);
            }

            String description = card.substring(0, fromIndex).trim();
            String startTime = card.substring(fromIndex + fromMarker.length(), toIndex).trim();
            String endTime = card.substring(toIndex + toMarker.length()).trim();

            if (description.isEmpty()) {
                throw new MissingDescriptionException(
                        DISCARD_PREFIX + "An event needs a description! " + eventUsage);
            }
            if (startTime.isEmpty() || endTime.isEmpty()) {
                throw new WrongCommandException(
                        DISCARD_PREFIX + "Please give both a start time and an end time! " + eventUsage);
            }

            Task newEvent = new Event(description, startTime, endTime);
            taskManager.addTask(newEvent);
            ui.showTaskAdded(newEvent, "Event", taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (isCommand(input, "delete")) {
            String card = extractArgument(input, "delete");
            if (card.isEmpty()) {
                throw new MissingDescriptionException(
                        DISCARD_PREFIX + "Please give a task number! Try: delete <task number>");
            }

            int taskNumber = parseTaskNumber(card, "delete", taskManager);
            Task deletedTask = taskManager.getTask(taskNumber - 1);
            taskManager.deleteTask(taskNumber - 1);
            ui.showTaskDeleted(deletedTask, taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (isCommand(input, "date")) {
            String dateString = extractArgument(input, "date");
            if (dateString.isEmpty()) {
                throw new MissingDescriptionException(
                        DISCARD_PREFIX + "Please give a date! Try: date <date>");
            }

            LocalDate targetDate = Deadline.parseDateOnly(dateString);

            taskManager.printTasksByDate(targetDate);
            return false;
        }

        if (isCommand(input, "find")) {
            String keyword = extractArgument(input, "find");
            if (keyword.isEmpty()) {
                throw new MissingDescriptionException(
                        DISCARD_PREFIX + "Please give a keyword to search for! Try: find <keyword>");
            }

            taskManager.printTasksByName(keyword);
            return false;
        }

        throw new WrongCommandException(DISCARD_PREFIX + "I do not understand the command \"" + input + "\"!");
    }

    /**
     * Returns whether input is the given command, either bare (e.g. "todo") or
     * followed by an argument (e.g. "todo borrow book"). Centralizes the
     * equals-or-starts-with-a-space check every argument-taking command needs,
     * so each one doesn't repeat it (and risk checking only one of the two
     * forms, as mark/unmark/delete previously did).
     *
     * @param input the raw line of user input.
     * @param command the command word to check for, with no trailing space.
     * @return true if input is exactly command, or command followed by a space and more text.
     */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Returns whatever follows the command word in input, with surrounding
     * whitespace trimmed and any run of internal whitespace (e.g. accidental
     * double spaces) collapsed to a single space. Collapsing internal
     * whitespace also means a marker like " /by " is still found correctly
     * even if the user typed extra spaces around it (e.g. "test  /by  2pm").
     * Assumes {@link #isCommand(String, String)} has already confirmed input
     * actually starts with command.
     *
     * @param input the raw line of user input.
     * @param command the command word to strip off the front.
     * @return the trimmed, whitespace-normalized remainder of input after command.
     */
    private static String extractArgument(String input, String command) {
        return input.substring(command.length()).trim().replaceAll("\\s+", " ");
    }

    /**
     * Parses and validates a task number argument, such as the "5" in "mark 5".
     * Confirms it's a valid integer and refers to a task actually in the list
     * (1-based, matching what the user sees from "list") before handing back a
     * usable 1-based task number.
     *
     * @param input the task number argument, expected to be a plain integer.
     * @param command the command word this argument was given to (e.g.
     *     "mark"), used to build a clear, command-specific error message if
     *     input isn't numeric.
     * @param taskManager the task list, used to check the number is in range.
     * @return the parsed, validated 1-based task number.
     * @throws WrongCommandException if input isn't a valid integer.
     * @throws WrongTaskNumberException if input is a valid integer but out of range.
     */
    private static int parseTaskNumber(String input, String command, TaskManager taskManager)
            throws AceException {
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new WrongCommandException(
                    DISCARD_PREFIX + "\"" + input + "\" isn't a valid task number! Try: "
                            + command + " <task number>");
        }

        if (taskNumber <= 0 || taskNumber > taskManager.getCurrentNumberOfTasks()) {
            throw new WrongTaskNumberException(taskNumber);
        }

        // Postcondition: every out-of-range case has already been thrown
        // above, so the value about to be returned should always be a valid,
        // in-range task number — this is the guarantee callers (e.g.
        // TaskManager's own bounds assertions) rely on. Asserting it here
        // would catch a bug if a future change to the checks above broke
        // that guarantee.
        assert taskNumber >= 1 && taskNumber <= taskManager.getCurrentNumberOfTasks()
                : "parseTaskNumber is returning an out-of-range value: " + taskNumber;

        return taskNumber;
    }
}
