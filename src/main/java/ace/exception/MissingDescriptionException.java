package ace.exception;

/**
 * Thrown when a command that requires a description (e.g. "todo", "deadline",
 * "event", "delete") is given without one, such as typing "todo" or "deadline "
 * with nothing after it.
 */
public class MissingDescriptionException extends AceException {

    /**
     * Creates a new exception for a command that was missing its required
     * description.
     *
     * @param task the name of the command that was missing a description (e.g. "todo").
     */
    public MissingDescriptionException(String task) {
        String toBePrinted = String.format(
                "Oh no! This card has to be discarded as I do not understand the command %s! ", task);
        super(toBePrinted);
    }
}
