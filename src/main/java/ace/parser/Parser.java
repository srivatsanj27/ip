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
            int taskNumber = parseTaskNumber(extractArgument(input, "mark"), input, taskManager);
            taskManager.markTask(taskNumber - 1);
            return false;
        }

        if (isCommand(input, "unmark")) {
            int taskNumber = parseTaskNumber(extractArgument(input, "unmark"), input, taskManager);
            taskManager.unmarkTask(taskNumber - 1);
            return false;
        }

        if (isCommand(input, "todo")) {
            String todoDescription = extractArgument(input, "todo");
            if (todoDescription.isEmpty()) {
                throw new MissingDescriptionException("todo");
            }

            Task newTodo = new Todo(todoDescription);
            taskManager.addTask(newTodo);
            ui.showTaskAdded(newTodo, "To-Do", taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (isCommand(input, "deadline")) {
            String card = extractArgument(input, "deadline");
            if (card.isEmpty()) {
                throw new MissingDescriptionException("deadline");
            }

            String byMarker = " /by ";
            int byIndex = card.indexOf(byMarker);
            if (byIndex == -1) {
                throw new WrongCommandException(input);
            }

            String description = card.substring(0, byIndex).trim();
            String byWhen = card.substring(byIndex + byMarker.length()).trim();

            Task newDeadline = new Deadline(description, byWhen);
            taskManager.addTask(newDeadline);
            ui.showTaskAdded(newDeadline, "Deadline", taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (isCommand(input, "event")) {
            String card = extractArgument(input, "event");
            if (card.isEmpty()) {
                throw new MissingDescriptionException("event");
            }

            String fromMarker = " /from ";
            String toMarker = " /to ";
            int fromIndex = card.indexOf(fromMarker);
            int toIndex = card.indexOf(toMarker);
            if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
                throw new WrongCommandException(input);
            }

            String description = card.substring(0, fromIndex).trim();
            String startTime = card.substring(fromIndex + fromMarker.length(), toIndex).trim();
            String endTime = card.substring(toIndex + toMarker.length()).trim();

            Task newEvent = new Event(description, startTime, endTime);
            taskManager.addTask(newEvent);
            ui.showTaskAdded(newEvent, "Event", taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (isCommand(input, "delete")) {
            String card = extractArgument(input, "delete");
            if (card.isEmpty()) {
                throw new MissingDescriptionException("delete");
            }

            int taskNumber = parseTaskNumber(card, input, taskManager);
            Task deletedTask = taskManager.getTask(taskNumber - 1);
            taskManager.deleteTask(taskNumber - 1);
            ui.showTaskDeleted(deletedTask, taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (isCommand(input, "date")) {
            String dateString = extractArgument(input, "date");
            if (dateString.isEmpty()) {
                throw new MissingDescriptionException("date");
            }

            LocalDate targetDate = Deadline.parseDateOnly(dateString);

            taskManager.printTasksByDate(targetDate);
            return false;
        }

        if (isCommand(input, "find")) {
            String keyword = extractArgument(input, "find");
            if (keyword.isEmpty()) {
                throw new MissingDescriptionException("find");
            }

            taskManager.printTasksByName(keyword);
            return false;
        }

        throw new WrongCommandException(input);
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
     * whitespace trimmed. Assumes {@link #isCommand(String, String)} has
     * already confirmed input actually starts with command.
     *
     * @param input the raw line of user input.
     * @param command the command word to strip off the front.
     * @return the trimmed remainder of input after command.
     */
    private static String extractArgument(String input, String command) {
        return input.substring(command.length()).trim();
    }

    /**
     * Parses and validates a task number argument, such as the "5" in "mark 5".
     * Confirms it's a valid integer and refers to a task actually in the list
     * (1-based, matching what the user sees from "list") before handing back a
     * usable 1-based task number.
     *
     * @param input the task number argument, expected to be a plain integer.
     * @param originalInput the full original command, used to build a clear error message if input isn't numeric.
     * @param taskManager the task list, used to check the number is in range.
     * @return the parsed, validated 1-based task number.
     * @throws WrongCommandException if input isn't a valid integer.
     * @throws WrongTaskNumberException if input is a valid integer but out of range.
     */
    private static int parseTaskNumber(String input, String originalInput, TaskManager taskManager)
            throws AceException {
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new WrongCommandException(originalInput);
        }

        if (taskNumber <= 0 || taskNumber > taskManager.getCurrentNumberOfTasks()) {
            throw new WrongTaskNumberException(taskNumber);
        }

        return taskNumber;
    }
}
