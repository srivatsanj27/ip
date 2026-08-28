package ace.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ace.exception.WrongDateFormatException;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class DeadlineTest {

    @Test
    public void constructor_fullDateTimeFormat_parsesCorrectly() throws WrongDateFormatException {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getByWhen());
    }

    @Test
    public void constructor_alternateDateTimeFormat_parsesCorrectly() throws WrongDateFormatException {
        Deadline deadline = new Deadline("return book", "2019-12-02 1800");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getByWhen());
    }

    @Test
    public void constructor_dateOnlyFormat_defaultsTimeToEndOfDay() throws WrongDateFormatException {
        Deadline deadline = new Deadline("buy car", "10/12/2026");
        assertEquals(LocalDateTime.of(2026, 12, 10, 23, 59), deadline.getByWhen());
    }

    @Test
    public void constructor_unparseableDate_wrongDateFormatExceptionThrown() {
        assertThrows(WrongDateFormatException.class, () -> new Deadline("bad deadline", "not-a-date"));
    }

    @Test
    public void constructor_emptyString_wrongDateFormatExceptionThrown() {
        assertThrows(WrongDateFormatException.class, () -> new Deadline("bad deadline", ""));
    }

    @Test
    public void constructor_directLocalDateTime_storesValueUnchanged() {
        LocalDateTime byWhen = LocalDateTime.of(2020, 1, 1, 9, 0);
        Deadline deadline = new Deadline("renew passport", byWhen);
        assertEquals(byWhen, deadline.getByWhen());
    }

    @Test
    public void toString_incompleteTask_formatsWithUncheckedBoxAndReadableDate() throws WrongDateFormatException {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        assertEquals("[D][ ] return book (by: Dec 02 2019 at 6:00pm)", deadline.toString());
    }

    @Test
    public void toString_completedTask_formatsWithCheckedBox() throws WrongDateFormatException {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        deadline.completeTask();
        assertEquals("[D][X] return book (by: Dec 02 2019 at 6:00pm)", deadline.toString());
    }

    @Test
    public void loadFormat_incompleteTask_usesMachineParseableDate() throws WrongDateFormatException {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        assertEquals("[D][ ] return book (by: 2/12/2019 1800)", deadline.loadFormat());
    }

    @Test
    public void loadFormat_completedTask_usesMachineParseableDate() throws WrongDateFormatException {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        deadline.completeTask();
        assertEquals("[D][X] return book (by: 2/12/2019 1800)", deadline.loadFormat());
    }
}
