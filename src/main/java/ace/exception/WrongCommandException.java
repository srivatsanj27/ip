package ace.exception;

public class WrongCommandException extends AceException {
    // handles inputs such as "what tasks do i have left " and "hello " etc.
    public WrongCommandException(String input) {
        String toBePrinted = String.format(
                "Oh no! This card has to be discarded as I do not understand the command %s! ", input);
        super(toBePrinted);
    }
}
