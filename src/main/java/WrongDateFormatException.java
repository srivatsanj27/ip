public class WrongDateFormatException extends AceException {
    public WrongDateFormatException(String input) {
        String toBePrinted = String.format("Oh no! I could not read %s as it is not in the expected format!", input);
        super(toBePrinted);
    }
}