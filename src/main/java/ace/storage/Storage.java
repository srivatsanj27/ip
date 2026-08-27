package ace.storage;

import ace.exception.WrongDateFormatException;
import ace.task.Deadline;
import ace.task.Event;
import ace.task.Task;
import ace.task.TaskManager;
import ace.task.Todo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Storage {
    private static final Path SAVED_PATH = Paths.get("data", "Ace.txt");
    private static final Path DIRECTORY_PATH = SAVED_PATH.getParent();

    /* every task now gets added to the hard disk, and every time the task is modified,
       by marking/unmarking/adding/deleting, the list is updated */
    public static void save(TaskManager taskManager) {
        if (!createPath()) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < taskManager.getCurrentNumberOfTasks(); i++) {
            Task task = taskManager.getTask(i);
            sb.append(task.loadFormat()).append(System.lineSeparator());
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

                try {
                    Task task = parseTask(line);
                    if (task != null) {
                        taskManager.addTask(task);
                    }
                } catch (IndexOutOfBoundsException | WrongDateFormatException e) {
                    System.out.println("Skipping a corrupted line in your save file: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Aw man! I'm not able to load your tasks due to this error:  " + e.getMessage());
        }
    }

    private static Task parseTask(String line) throws WrongDateFormatException {
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

        if (task != null && isMarked) {
            task.completeTask();
        }

        return task;
    }

    // 2-step process of first checking whether the parent folder exists. if no, it gets created
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