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
        System.out.println(" Hello! I'm ACE, your personal task manager!");
        System.out.println(" What can I do for you?");
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();

        while (true) {
            String input = scanner.nextLine();

            System.out.println(line);

            if (input.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }
            else if (input.equals("list")) {
                System.out.println("Here are the current tasks in your list: ");
                taskManager.printTasks();
            }
            else if (input.startsWith("mark ")) {
                int arrayIndex = Integer.parseInt(input.substring(5)) - 1;
                taskManager.markTask(arrayIndex);
            }
            else if (input.startsWith("unmark ")) {
                int arrayIndex = Integer.parseInt(input.substring(7)) - 1;
                taskManager.unmarkTask(arrayIndex);
            }
            else if (input.startsWith("todo ")) {
                String todoDescription = input.substring(5).trim();
                Task newTodo = new Todo(todoDescription);
                taskManager.addTask(newTodo);

                System.out.println("Got it, I have now added a new To-Do task!");
                System.out.println(newTodo.toString());
                String totalTaskMessage = String.format("You now have a total of %d tasks in your list!%n", taskManager.getCurrentNumberOfTasks());
                System.out.print(totalTaskMessage);
            }
            else if (input.startsWith("deadline ")) {
                String detail = input.substring(9).trim();

                int byIndex = detail.indexOf(" /by ");

                if (byIndex != -1) {
                    String description = detail.substring(0, byIndex).trim();
                    String byWhen = detail.substring(byIndex + 5).trim();

                    Task newDeadline = new Deadline(description, byWhen);
                    taskManager.addTask(newDeadline);

                    System.out.println("Got it, I have now added a new Deadline task!");
                    System.out.println(newDeadline.toString());
                    String totalTaskMessage = String.format("You now have a total of %d tasks in your list!%n", taskManager.getCurrentNumberOfTasks());
                    System.out.print(totalTaskMessage);
                }
            }
            else if (input.startsWith("event ")) {
                String detail = input.substring(6).trim();

                int fromIndex = detail.indexOf(" /from ");
                int toIndex = detail.indexOf(" /to ");

                if (fromIndex != -1 && toIndex != -1 && fromIndex < toIndex) {
                    String description = detail.substring(0, fromIndex).trim();
                    String startTime = detail.substring(fromIndex + 7, toIndex).trim();
                    String endTime = detail.substring(toIndex + 5).trim();

                    Task newEvent = new Event(description, startTime, endTime);
                    taskManager.addTask(newEvent);

                    System.out.println("Got it, I have now added a new Event task!");
                    System.out.println(newEvent.toString());
                    String totalTaskMessage = String.format("You now have a total of %d tasks in your list!%n", taskManager.getCurrentNumberOfTasks());
                    System.out.print(totalTaskMessage);
                }
            }
            System.out.println(line);
        }
        scanner.close();
    }
}