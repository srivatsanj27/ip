package ace.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import ace.storage.Storage;

/**
 * Holds the user's task list and provides operations to add, remove, mark, and
 * query tasks. Every mutating operation automatically persists the updated list
 * to disk via {@link Storage}, so callers never need to save explicitly.
 */
public class TaskManager {
    private static final int MAX_TASKS = 100;
    private int numTasks = 0;
    private final Task[] tasks;

    /**
     * Creates a new, empty task manager.
     */
    public TaskManager() {
        this.tasks = new Task[MAX_TASKS];
    }

    /**
     * Adds a task to the end of the list, then saves the updated list to disk.
     *
     * @param task the task to add.
     */
    public void addTask(Task task) {
        tasks[numTasks] = task;
        numTasks++;
        Storage.save(this);
    }

    /**
     * Removes the task at the given index, shifting every later task left by one
     * to close the gap, then saves the updated list to disk.
     *
     * @param index the zero-based index of the task to remove.
     */
    public void deleteTask(int index) {
        for (int i = index + 1; i < this.getCurrentNumberOfTasks(); i++) {
            this.tasks[i - 1] = this.tasks[i];
        }

        this.tasks[numTasks - 1] = null;
        numTasks--;

        Storage.save(this);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index the zero-based index of the task to retrieve.
     * @return the task at that index.
     */
    public Task getTask(int index) {
        return tasks[index];
    }

    /**
     * Returns how many tasks are currently in the list.
     *
     * @return the current number of tasks.
     */
    public int getCurrentNumberOfTasks() {
        return numTasks;
    }

    /**
     * Marks the task at the given index as completed, prints a confirmation, and
     * saves the updated list to disk.
     *
     * @param index the zero-based index of the task to mark.
     */
    public void markTask(int index) {
        this.tasks[index].completeTask();
        System.out.println("You have checked this card!");
        System.out.println(tasks[index]);

        Storage.save(this);
    }

    /**
     * Marks the task at the given index as not completed, prints a confirmation,
     * and saves the updated list to disk.
     *
     * @param index the zero-based index of the task to unmark.
     */
    public void unmarkTask(int index) {
        this.tasks[index].resetTask();
        System.out.println("You have unchecked this card again!");
        System.out.println(tasks[index]);

        Storage.save(this);
    }

    /**
     * Prints every task in the list, numbered from 1, or a message if the list is
     * empty.
     */
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

    /**
     * Prints every {@link Deadline} in the list that is due on the given date,
     * numbered from 1, or a message if none are due that day. Tasks that aren't
     * deadlines (e.g. todos, events) are never included.
     *
     * @param targetDate the date to filter deadlines by.
     */
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
    /**
     * Prints every task in the list whose description contains the given text,
     * numbered from 1, or a message if none match.
     *
     * @param name the text to search for within each task's description.
     */
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
