package ace.exception;

public class MissingDescriptionException extends AceException {
    // handles inputs such as "deadline " and "todo " etc.
    public MissingDescriptionException(String task) {
        String toBePrinted = String.format(
                "Oh no! This card has to be discarded as I do not understand the command %s! ", task);
        super(toBePrinted);
    }
}
