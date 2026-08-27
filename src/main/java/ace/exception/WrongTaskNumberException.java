package ace.exception;

public class WrongTaskNumberException extends AceException {
    public WrongTaskNumberException(int taskNumber) { /* handles inputs such as "mark 5 " when there are only 4 tasks */
        String toBePrinted = String.format("Oh no! This card has to be discarded as I do not see the number %d in your hand! ", taskNumber);
        super(toBePrinted);
    }
}