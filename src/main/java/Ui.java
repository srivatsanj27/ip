import java.util.Scanner;

public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public String readCommand() {
        return scanner.nextLine().trim();
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public void showWelcome() {
        String logo = "   ___   _____  _____\n"
                + "  / _ \\ /  __ \\|  ___|\n"
                + " / /_\\ \\| /  \\| |__  \n"
                + " |  _  || |    |  __| \n"
                + " | | | || \\__/\\| |___ \n"
                + " \\_| |_/ \\____/\\____/\n";

        System.out.println("Hello from\n" + logo);
        showLine();
        System.out.println(" Hello! I'm ACE, your personal poker-themed task manager!");
        System.out.println(" Shall we begin our game?");
        showLine();
    }

    public void showGoodbye() {
        System.out.println("Cashing out! See you again soon!");
    }

    public void showError(String message) {
        System.out.println(message);
    }

    public void showTaskAdded(Task task, String taskType, int totalTasks) {
        System.out.println("Great! I've added a new " + taskType + " card to your hand!");
        System.out.println(task);
        System.out.printf("You now have a total of %d cards in your hand!%n", totalTasks);
    }

    public void showTaskDeleted(Task task, int totalTasks) {
        System.out.println("Got it, you have discarded this card!");
        System.out.println(task);
        System.out.printf("You now have a total of %d cards in your hand!%n", totalTasks);
    }

    public void showTaskMarked(Task task) {
        System.out.println("You have folded this card!");
        System.out.println(task);
    }

    public void showTaskUnmarked(Task task) {
        System.out.println("You have been dealt this card again!");
        System.out.println(task);
    }
}