public class WrongCommandException extends AceException {
    public WrongCommandException(String input) { /* handles inputs such as "what tasks do i have left " and "hello " etc. */
        String toBePrinted = String.format("Oh no! This card has to be discarded as I do not understand the command %s! ", input);
        super(toBePrinted);
    }
}
