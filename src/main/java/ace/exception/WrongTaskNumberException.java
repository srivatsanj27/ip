package ace.exception;

public class WrongTaskNumberException extends AceException {
    // handles inputs such as "mark 5 " when there are only 4 tasks
    public WrongTaskNumberException(int taskNumber) {
        String toBePrinted = String.format(
                "Oh no! This card has to be discarded as I do not see the number %d in your hand! ", taskNumber);
        super(toBePrinted);
    }
}
