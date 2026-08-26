import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Ace {
    public static void main(String[] args) {
        String line = "____________________________________________________________";
        String ACE_Logo = "   ___   _____  _____\n"
                + "  / _ \\ /  __ \\|  ___|\n"
                + " / /_\\ \\| /  \\| |__  \n"
                + " |  _  || |    |  __| \n"
                + " | | | || \\__/\\| |___ \n"
                + " \\_| |_/ \\____/\\____/\n";

        System.out.println("Hello from\n" + ACE_Logo);
        System.out.println(line);
        System.out.println(" Hello! I'm ACE, your personal poker-themed task manager!");
        System.out.println(" Shall we begin our game?");
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();

        // now, any saved tasks will be loaded automatically when the chat loads up again
        Storage.load(taskManager);

        while (true) {
            String input = scanner.nextLine().trim();

            System.out.println(line);

            try {
                boolean canProcess = handleInput(input, taskManager);
                if (canProcess) {
                    break;
                }
            }
            catch (AceException exception) {
                System.out.print(exception.getMessage() + "\n");
            }

            System.out.println(line);
        }

        scanner.close();
    }

    private static boolean handleInput(String input, TaskManager taskManager) throws AceException {
        if (input.equals("list")) {
            System.out.println("Here's your hand!");
            taskManager.printTasks(); // lists out all tasks
            return false;
        }
        if (input.equals("bye")) { // leaves the user with a goodbye message
            System.out.println("Cashing out! See you again soon!");
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
            if (todoDescription.isEmpty()) { // ensures inputs such as "todo " is caught
                throw new MissingDescriptionException("todo");
            }

            Task newTodo = new Todo(todoDescription);
            taskManager.addTask(newTodo);
            printTaskAdded(newTodo, taskManager, "To-Do");
            return false;
        }

        if (input.equals("deadline") || input.startsWith("deadline ")) {
            String card = input.substring(8).trim();
            if (card.isEmpty()) {
                throw new MissingDescriptionException("deadline"); // same logic as todo's nested if-loop
            }

            int byIndex = card.indexOf(" /by ");

            if (byIndex == -1) {
                throw new WrongCommandException(input);
            }

            String description = card.substring(0, byIndex).trim();
            String byWhen = card.substring(byIndex + 5).trim();

            Task newDeadline = new Deadline(description, byWhen);
            taskManager.addTask(newDeadline);
            printTaskAdded(newDeadline, taskManager, "Deadline");
            return false;
        }

        if (input.equals("event") || input.startsWith("event ")) {
            String card = input.substring(5).trim();

            if (card.isEmpty()) {
                throw new MissingDescriptionException("event"); // same logic as other two nested if-loops
            }

            int fromIndex = card.indexOf(" /from ");
            int toIndex = card.indexOf(" /to ");
            if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
                throw new WrongCommandException(input); // ensures that the starting and ending times are physically possible
            }

            String description = card.substring(0, fromIndex).trim();
            String startTime = card.substring(fromIndex + 7, toIndex).trim();
            String endTime = card.substring(toIndex + 5).trim();

            Task newEvent = new Event(description, startTime, endTime);
            taskManager.addTask(newEvent);
            printTaskAdded(newEvent, taskManager, "Event");
            return false;
        }

        if (input.startsWith("delete ")) {
            String card = input.substring(6).trim();

            if (card.isEmpty()) {
                throw new MissingDescriptionException("delete"); // same logic as other two nested if-loops
            }

            int taskNumber = parseTaskNumber(card, input, taskManager);
            Task deletedTask = taskManager.getTask(taskNumber - 1);
            taskManager.deleteTask(taskNumber - 1);
            printTaskDeleted(deletedTask, taskManager);
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
            }
            catch (DateTimeParseException e) {
                throw new WrongDateFormatException(dateString);
            }

            taskManager.printTasksByDate(targetDate);
            return false;
        }

        throw new WrongCommandException(input);
    }

    // helper function to ensure that the 3 lines are printed after each task, regardless of type, share the same format
    private static void printTaskAdded(Task task, TaskManager taskManager, String taskType) {
        System.out.println("Great! I've added a new " + taskType + " card to your hand!");

        System.out.println(task.toString());

        String totalTaskMessage = String.format("You now have a total of %d cards in your hand!\n",
                taskManager.getCurrentNumberOfTasks());

        System.out.print(totalTaskMessage);
    }

    // similar helper function to the prev one, but this one prints for the delete if conditional
    private static void printTaskDeleted(Task task, TaskManager taskManager) {
        System.out.println("Got it, you have discarded this card!");

        System.out.println(task.toString());

        String totalTaskMessage = String.format("You now have a total of %d cards in your hand!\n",
                taskManager.getCurrentNumberOfTasks());

        System.out.print(totalTaskMessage);
    }

    private static int parseTaskNumber(String input, String originalInput, TaskManager taskManager)
            throws AceException {
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(input);
        }
        catch (NumberFormatException e) { // ensures that only valid numbers follow task type inputs
            throw new WrongCommandException(originalInput);
        }

        if (taskNumber <= 0 || taskNumber > taskManager.getCurrentNumberOfTasks()) {
            throw new WrongTaskNumberException(taskNumber);
        }

        return taskNumber;
    }
}