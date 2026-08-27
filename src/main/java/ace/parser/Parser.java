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

public class Parser {
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