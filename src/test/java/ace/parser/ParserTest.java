package ace.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ace.exception.MissingDescriptionException;
import ace.exception.WrongCommandException;
import ace.exception.WrongDateFormatException;
import ace.exception.WrongTaskNumberException;
import ace.task.TaskManager;
import ace.ui.Ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Parser.parseCommand delegates to TaskManager, whose mutating methods call
 * Storage.save() against the real data/Ace.txt — back up/restore it around
 * each test, same reasoning as TaskManagerTest/StorageTest.
 */
public class ParserTest {
    private static final Path SAVE_FILE = Paths.get("data", "Ace.txt");
    private byte[] backup;
    private boolean fileExisted;
    private TaskManager taskManager;
    private Ui ui;

    @BeforeEach
    public void setUp() throws IOException {
        fileExisted = Files.exists(SAVE_FILE);
        if (fileExisted) {
            backup = Files.readAllBytes(SAVE_FILE);
        }
        taskManager = new TaskManager();
        ui = new Ui();
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
    public void parseCommand_validTodo_addsTaskAndReturnsFalse() throws Exception {
        boolean isExit = Parser.parseCommand("todo borrow book", taskManager, ui);

        assertFalse(isExit);
        assertEquals(1, taskManager.getCurrentNumberOfTasks());
    }

    @Test
    public void parseCommand_bye_returnsTrue() throws Exception {
        boolean isExit = Parser.parseCommand("bye", taskManager, ui);

        assertTrue(isExit);
    }

    @Test
    public void parseCommand_todoMissingDescription_missingDescriptionExceptionThrown() {
        assertThrows(MissingDescriptionException.class,
                () -> Parser.parseCommand("todo", taskManager, ui));
    }

    @Test
    public void parseCommand_unknownCommand_wrongCommandExceptionThrown() {
        assertThrows(WrongCommandException.class,
                () -> Parser.parseCommand("gibberish", taskManager, ui));
    }

    @Test
    public void parseCommand_markValidNumber_marksTaskComplete() throws Exception {
        Parser.parseCommand("todo borrow book", taskManager, ui);

        Parser.parseCommand("mark 1", taskManager, ui);

        assertTrue(taskManager.getTask(0).getCompleted());
    }

    @Test
    public void parseCommand_markNonNumericArgument_wrongCommandExceptionThrown() {
        assertThrows(WrongCommandException.class,
                () -> Parser.parseCommand("mark abc", taskManager, ui));
    }

    @Test
    public void parseCommand_markOutOfRangeNumber_wrongTaskNumberExceptionThrown() {
        assertThrows(WrongTaskNumberException.class,
                () -> Parser.parseCommand("mark 5", taskManager, ui));
    }

    @Test
    public void parseCommand_deadlineMissingByClause_wrongCommandExceptionThrown() {
        assertThrows(WrongCommandException.class,
                () -> Parser.parseCommand("deadline return book", taskManager, ui));
    }

    @Test
    public void parseCommand_deadlineUnparseableDate_wrongDateFormatExceptionThrown() {
        assertThrows(WrongDateFormatException.class,
                () -> Parser.parseCommand("deadline return book /by not-a-date", taskManager, ui));
    }
}
