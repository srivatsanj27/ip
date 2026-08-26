public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String loadFormat() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}