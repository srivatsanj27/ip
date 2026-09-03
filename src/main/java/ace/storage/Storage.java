package ace.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ace.exception.WrongDateFormatException;
import ace.task.Deadline;
import ace.task.Event;
import ace.task.Task;
import ace.task.TaskManager;
import ace.task.Todo;

/**
 * Handles saving the task list to disk and loading it back on startup. Tasks are
 * persisted one per line using each task's {@link Task#loadFormat()}, which is
 * kept separate from its display format so that changing how a task looks on
 * screen doesn't break the save file's format.
 */
public class Storage {
    private static final Path SAVED_PATH = Paths.get("data", "Ace.txt");
    private static final Path DIRECTORY_PATH = SAVED_PATH.getParent();

    /**
     * Writes every task currently in taskManager to the save file, overwriting
     * whatever was there before. Called automatically by {@link TaskManager} after
     * every mutation (add/delete/mark/unmark), so the file on disk always reflects
     * the current in-memory state.
     *
     * @param taskManager the task manager whose current tasks should be saved.
     */
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

    /**
     * Reads the save file (if it exists) and repopulates taskManager with its
     * contents. Does nothing if the file or its folder don't exist yet, which is
     * the expected case on a fresh machine that has never saved anything. Any
     * individual line that can't be parsed is skipped with a warning rather than
     * aborting the whole load, so one corrupted line can't take down startup.
     *
     * @param taskManager the task manager to populate with the loaded tasks.
     */
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

    /**
     * Parses a single save-file line back into the Task it represents. Each line
     * matches the format produced by {@link Task#loadFormat()}, e.g.
     * "[T][ ] description", "[D][X] description (by: ...)", or
     * "[E][ ] description (from: ... to: ...)" — the type character at index 1
     * and the mark character at index 4 are always at those fixed positions, with
     * the description and any extra fields starting at index 7.
     *
     * @param line a single line read from the save file.
     * @return the task the line represents, or null if its type character isn't recognized.
     * @throws WrongDateFormatException if the line is a Deadline with an unparseable date.
     */
    private static Task parseTask(String line) throws WrongDateFormatException {
        char type = line.charAt(1);
        boolean isMarked = line.charAt(4) == 'X';
        String rest = line.substring(7);

        Task task = null;

        if (type == 'T') {
            task = new Todo(rest);
        } else if (type == 'D') {
            int byIndex = rest.lastIndexOf(" (by: ");

            String desc = rest.substring(0, byIndex);
            String by = rest.substring(byIndex + 6, rest.length() - 1);

            task = new Deadline(desc, by);
        } else if (type == 'E') {
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

    /**
     * Ensures the save file's parent folder exists, creating it if it doesn't.
     * Called before every write, since a fresh checkout won't have a data/ folder
     * yet.
     *
     * @return true if the folder exists (or was just created), false if it couldn't be created.
     */
    private static boolean createPath() {
        if (!Files.exists(DIRECTORY_PATH)) {
            try {
                Files.createDirectories(DIRECTORY_PATH);
            } catch (IOException e) {
                System.out.println("I could not create this directory due to this: " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}
