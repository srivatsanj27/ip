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
        System.out.println(" Hello! I'm ACE!");
        System.out.println(" What can I do for you?");
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();

            System.out.println(line);

            if (input.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }
            System.out.println(" " + input);
            System.out.println(line);
        }

        scanner.close();
    }
}