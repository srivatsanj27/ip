import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Storage {
    private static final Path SAVED_PATH = Paths.get("data", "Ace.txt");
    private static final Path DIRECTORY_PATH = SAVED_PATH.getParent();

    /* every task now gets added to the hard disk, and every time the task is modified,
       by marking/unmarking/adding/deleting, the list is updated
     */
    public static void save(TaskManager taskManager) {
        if (!createPath()) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < taskManager.getCurrentNumberOfTasks(); i++) {
            Task task = taskManager.getTask(i);
            sb.append(task.toString()).append(System.lineSeparator());
        }

        try {
            Files.writeString(SAVED_PATH, sb.toString());
        } catch (IOException e) {
            System.out.println("Oh no! I ran into an error of " + e.getMessage());
        }
    }

    public static void load(TaskManager taskManager) {
        if (!Files.exists(SAVED_PATH)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(SAVED_PATH);
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                // the sysout message for each task follows it's toString representation
                char type = line.charAt(1);
                boolean isMarked = line.charAt(4) == 'X';

                String rest = line.substring(7);
                Task task = null;

                if (type == 'T') {
                    task = new Todo(rest);
                }
                else if (type == 'D') {
                    int byIndex = rest.lastIndexOf(" (by: ");
                    String desc = rest.substring(0, byIndex);
                    String by = rest.substring(byIndex + 6, rest.length() - 1);

                    task = new Deadline(desc, by);
                }
                else if (type == 'E') {
                    int fromIndex = rest.lastIndexOf(" (from: ");
                    int toIndex = rest.lastIndexOf(" to: ");

                    String desc = rest.substring(0, fromIndex);
                    String startTime = rest.substring(fromIndex + 8, toIndex);
                    String endTime = rest.substring(toIndex + 5, rest.length() - 1);

                    task = new Event(desc, startTime, endTime);
                }

                if (task != null) {
                    if (isMarked) {
                        task.completeTask();
                    }
                    taskManager.addTask(task);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Aw man! I'm not able to load your tasks due to this error:  " + e.getMessage());
        }
    }

    // 2 step procress of first checking whether the parent folder exists. if no, it gets created
    private static boolean createPath() {
        if (!Files.exists(DIRECTORY_PATH)) {
            try {
                Files.createDirectories(DIRECTORY_PATH);
            }
            catch (IOException e) {
                System.out.println("I could not create this directory due to this: " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}