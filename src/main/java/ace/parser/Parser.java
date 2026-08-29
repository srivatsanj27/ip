package ace.parser;

import ace.exception.AceException;
import ace.exception.MissingDescriptionException;
import ace.exception.WrongCommandException;
import ace.exception.WrongDateFormatException;
import ace.exception.WrongTaskNumberException;
import ace.task.Deadline;
import ace.task.Event;
import ace.task.Task;
import ace.task.TaskManager;
import ace.task.Todo;
import ace.ui.Ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
        if (input.equals("list")) {
            taskManager.printTasks();
            return false;
        }
        if (input.equals("bye")) {
            ui.showGoodbye();
            return true;
        }
        if (input.startsWith("mark ")) {
            int taskNumber = parseTaskNumber(input.substring(5).trim(), input, taskManager);
            taskManager.markTask(taskNumber - 1);
            return false;
        }

        if (input.startsWith("unmark ")) {
            int taskNumber = parseTaskNumber(input.substring(7).trim(), input, taskManager);
            taskManager.unmarkTask(taskNumber - 1);
            return false;
        }

        if (input.equals("todo") || input.startsWith("todo ")) {
            String todoDescription = input.substring(4).trim();
            if (todoDescription.isEmpty()) {
                throw new MissingDescriptionException("todo");
            }

            Task newTodo = new Todo(todoDescription);
            taskManager.addTask(newTodo);
            ui.showTaskAdded(newTodo, "To-Do", taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (input.equals("deadline") || input.startsWith("deadline ")) {
            String card = input.substring(8).trim();
            if (card.isEmpty()) {
                throw new MissingDescriptionException("deadline");
            }

            int byIndex = card.indexOf(" /by ");
            if (byIndex == -1) {
                throw new WrongCommandException(input);
            }

            String description = card.substring(0, byIndex).trim();
            String byWhen = card.substring(byIndex + 5).trim();

            Task newDeadline = new Deadline(description, byWhen);
            taskManager.addTask(newDeadline);
            ui.showTaskAdded(newDeadline, "Deadline", taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (input.equals("event") || input.startsWith("event ")) {
            String card = input.substring(5).trim();
            if (card.isEmpty()) {
                throw new MissingDescriptionException("event");
            }

            int fromIndex = card.indexOf(" /from ");
            int toIndex = card.indexOf(" /to ");
            if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
                throw new WrongCommandException(input);
            }

            String description = card.substring(0, fromIndex).trim();
            String startTime = card.substring(fromIndex + 7, toIndex).trim();
            String endTime = card.substring(toIndex + 5).trim();

            Task newEvent = new Event(description, startTime, endTime);
            taskManager.addTask(newEvent);
            ui.showTaskAdded(newEvent, "Event", taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (input.startsWith("delete ")) {
            String card = input.substring(6).trim();
            if (card.isEmpty()) {
                throw new MissingDescriptionException("delete");
            }

            int taskNumber = parseTaskNumber(card, input, taskManager);
            Task deletedTask = taskManager.getTask(taskNumber - 1);
            taskManager.deleteTask(taskNumber - 1);
            ui.showTaskDeleted(deletedTask, taskManager.getCurrentNumberOfTasks());
            return false;
        }

        if (input.equals("date") || input.startsWith("date ")) {
            String dateString = input.substring(4).trim();
            if (dateString.isEmpty()) {
                throw new MissingDescriptionException("date");
            }

            LocalDate targetDate;
            try {
                targetDate = LocalDate.parse(dateString, Deadline.DATE_ONLY_INPUT);
            } catch (DateTimeParseException e) {
                throw new WrongDateFormatException(dateString);
            }

            taskManager.printTasksByDate(targetDate);
            return false;
        }

        throw new WrongCommandException(input);
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