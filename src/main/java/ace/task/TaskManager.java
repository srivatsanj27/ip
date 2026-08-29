package ace.task;

import ace.storage.Storage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        Storage.save(this);
    }

    public void deleteTask(int index) {
        for (int i = index + 1; i < this.getCurrentNumberOfTasks(); i++) {
            this.tasks[i - 1] = this.tasks[i];
        }

        this.tasks[numTasks - 1] = null;
        numTasks--;

        Storage.save(this);
    }

    public Task getTask(int index) {
        return tasks[index];
    }

    public int getCurrentNumberOfTasks() {
        return numTasks;
    }

    public void markTask(int index) {
        this.tasks[index].completeTask();
        System.out.println("You have checked this card!");
        System.out.println(tasks[index]);

        Storage.save(this);
    }

    public void unmarkTask(int index) {
        this.tasks[index].resetTask();
        System.out.println("You have unchecked this card again!");
        System.out.println(tasks[index]);

        Storage.save(this);
    }

    public void printTasks() {
        if (this.getCurrentNumberOfTasks() == 0) {
            System.out.println("Your hand is currently empty!");
            return;
        } else {
            for (int i = 0; i < this.getCurrentNumberOfTasks(); i++) {
                System.out.println(" " + (i + 1) + "." + tasks[i]);
            }
        }
    }

    public void printTasksByDate(LocalDate targetDate) {
        String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        System.out.println("Here are your cards due on " + formattedDate + ":");

        int count = 0;
        for (int i = 0; i < numTasks; i++) {
            Task task = tasks[i];

            if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                // Compare only the LocalDate part of the deadline's LocalDateTime
                if (deadline.getByWhen().toLocalDate().equals(targetDate)) {
                    System.out.println(" " + (count + 1) + "." + deadline);
                    count++;
                }
            }
        }

        if (count == 0) {
            System.out.println(" No cards due on this date!");
        }
    }

    public void printTasksByName(String name) {
        System.out.println("Here are your matching cards which include the name " + name + ":");

        int count = 0;
        for (int i = 0; i < numTasks; i++) {
            Task task = tasks[i];

            if (task.getDescription().contains(name)) {
                System.out.println(" " + (count + 1) + "." + task);
                count++;
            }
        }

        if (count == 0) {
            System.out.println("You do not have any such cards!");
        }
    }
}