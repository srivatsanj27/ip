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
            else {
                Task newTask = new Task(input);
                taskManager.addTask(newTask);
                System.out.println("added: " + input);
            }

            System.out.println(line);
        }

        scanner.close();
    }
}