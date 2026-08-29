package ace.storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ace.task.Deadline;
import ace.task.Task;
import ace.task.TaskManager;
import ace.task.Todo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Storage's save path (data/Ace.txt) is hardcoded, not injectable, so these tests
 * back up and restore the developer's real save file around each test rather than
 * risk losing it.
 */
public class StorageTest {
    private static final Path SAVE_FILE = Paths.get("data", "Ace.txt");
    private byte[] backup;
    private boolean fileExisted;

    @BeforeEach
    public void backUpRealSaveFile() throws IOException {
        fileExisted = Files.exists(SAVE_FILE);
        if (fileExisted) {
            backup = Files.readAllBytes(SAVE_FILE);
        }
    }

    @AfterEach
    public void restoreRealSaveFile() throws IOException {
        if (fileExisted) {
            Files.write(SAVE_FILE, backup);
        } else {
            Files.deleteIfExists(SAVE_FILE);
        }
    }

    @Test
    public void saveThenLoad_mixOfTaskTypes_roundTripsCorrectly() throws Exception {
        TaskManager original = new TaskManager();
        original.addTask(new Todo("borrow book"));
        original.addTask(new Deadline("return book", "2/12/2019 1800"));
        original.getTask(0).completeTask();

        Storage.save(original);

        TaskManager loaded = new TaskManager();
        Storage.load(loaded);

        assertEquals(2, loaded.getCurrentNumberOfTasks());
        assertEquals(original.getTask(0).toString(), loaded.getTask(0).toString());
        assertEquals(original.getTask(1).toString(), loaded.getTask(1).toString());
    }

    @Test
    public void load_fileDoesNotExist_leavesTaskManagerEmpty() throws IOException {
        Files.deleteIfExists(SAVE_FILE);

        TaskManager taskManager = new TaskManager();
        Storage.load(taskManager);

        assertEquals(0, taskManager.getCurrentNumberOfTasks());
    }

    @Test
    public void load_corruptedLineAmongGoodOnes_skipsBadLineWithoutCrashing() throws IOException {
        Files.createDirectories(SAVE_FILE.getParent());
        Files.writeString(SAVE_FILE,
                "[T][ ] good task\n"
                + "bad\n"
                + "[D][ ] broken deadline (by: not-a-date)\n");

        TaskManager taskManager = new TaskManager();
        assertDoesNotThrow(() -> Storage.load(taskManager));

        assertEquals(1, taskManager.getCurrentNumberOfTasks());
        Task onlyTask = taskManager.getTask(0);
        assertEquals("[T][ ] good task", onlyTask.toString());
    }
}
