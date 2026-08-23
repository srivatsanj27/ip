public class MissingDescriptionException extends AceException {
    public MissingDescriptionException(String task) { /* handles inputs such as "deadline " and "todo " etc. */
        String toBePrinted = String.format("Oh no! This card has to be discarded as I do not understand the command %s! ", task);
        super(toBePrinted);
    }
}