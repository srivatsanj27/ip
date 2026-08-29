package ace.exception;

/**
 * Thrown when user input isn't a command Ace recognizes at all, such as an
 * unrecognized command word, or a recognized command with malformed arguments
 * (e.g. a non-numeric argument to "mark", or a "deadline"/"event" missing its
 * required "/by", "/from", or "/to" clause).
 */
public class WrongCommandException extends AceException {

    /**
     * Creates a new exception for input that couldn't be understood as a command.
     *
     * @param input the full, unrecognized or malformed user input.
     */
    public WrongCommandException(String input) {
        String toBePrinted = String.format(
                "Oh no! This card has to be discarded as I do not understand the command %s! ", input);
        super(toBePrinted);
    }
}
