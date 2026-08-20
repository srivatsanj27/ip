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

    public void completeTask() {
        this.isCompleted = true;
    }

    public void resetTask() {
        this.isCompleted = false;
    }

    public boolean getCompleted() {
        return this.isCompleted;
    }

    @Override
    public String toString() {
        if (this.isCompleted) {
            return "[X] " + this.getDescription();
        }
        else {
            return "[ ] " + this.getDescription();
        }
    }
}