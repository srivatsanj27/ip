public class Task {
    public String description;
    public boolean isCompleted;

    public Task(String description) {
        this.description = description;
        this.isCompleted = false;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getCompleted() {
        return this.isCompleted;
    }

    @Override
    public String toString() {
        return this.getDescription();
    }
}