public class TaskManager {
    private final static int maxTasks = 100;
    private int numTasks = 0;
    private Task[] tasks;

    public TaskManager() {
        this.tasks = new Task[100];
    }

    public void addTask(Task task) {
        tasks[numTasks] = task;
        numTasks++;
    }

    public Task getTask(int index) {
        return tasks[index];
    }

    public int getCurrentNumberOfTasks() {
        return numTasks;
    }

    public void markTask(int index) {
        this.tasks[index].completeTask();
        System.out.println("This task has been marked as complete!");
        System.out.println(tasks[index]);
    }

    public void unmarkTask(int index) {
        this.tasks[index].resetTask();
        System.out.println("This task has been marked as not complete again!");
        System.out.println(tasks[index]);
    }

    public void printTasks() {
        if (this.getCurrentNumberOfTasks() == 0) {
            System.out.println("You currently do not have any outstanding tasks!");
            return;
        }
        else {
            for (int i = 0; i < this.getCurrentNumberOfTasks(); i++) {
                System.out.println(" " + (i + 1) + "." + tasks[i]);
            }
        }
    }
}