public class Deadline extends Task {
    private String byWhen;

    public Deadline(String description, String byWhen) {
        super(description);
        this.byWhen = byWhen;
    }

    public String getByWhen() {
        return this.byWhen;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.getByWhen() + ")";
    }
}