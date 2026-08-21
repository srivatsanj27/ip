abstract public class Task { /* this is not an abstract class to ensure no 'Task' itself is instantiated, without
                                    specifying what type of task it is */
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