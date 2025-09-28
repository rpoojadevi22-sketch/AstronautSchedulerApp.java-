import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Main class
public class AstronautSchedulerApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ScheduleManager manager = ScheduleManager.getInstance();

        while (true) {
            System.out.println("\n=== Astronaut Daily Schedule ===");
            System.out.println("1. Add Task");
            System.out.println("2. Remove Task");
            System.out.println("3. View Tasks");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Enter description: ");
                    String desc = scanner.nextLine();
                    System.out.print("Enter start time (HH:mm): ");
                    String start = scanner.nextLine();
                    System.out.print("Enter end time (HH:mm): ");
                    String end = scanner.nextLine();
                    System.out.print("Enter priority (High/Medium/Low): ");
                    String priority = scanner.nextLine();

                    if (!TimeValidator.isValidTime(start) || !TimeValidator.isValidTime(end)) {
                        System.out.println("Error: Invalid time format.");
                        break;
                    }

                    Task task = Task.create(desc, start, end, priority);
                    manager.addTask(task);
                    break;

                case 2:
                    System.out.print("Enter description to remove: ");
                    String removeDesc = scanner.nextLine();
                    manager.removeTask(removeDesc);
                    break;

                case 3:
                    manager.viewTasks();
                    break;

                case 4:
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}

// Singleton Schedule Manager
class ScheduleManager {
    private static ScheduleManager instance;
    private final List<Task> tasks;

    private ScheduleManager() {
        this.tasks = new ArrayList<>();
    }

    public static synchronized ScheduleManager getInstance() {
        if (instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    public void addTask(Task task) {
        for (Task t : tasks) {
            if (task.overlapsWith(t)) {
                System.out.println("Error: Task conflicts with existing task \"" + t.getDescription() + "\"");
                return;
            }
        }
        tasks.add(task);
        tasks.sort(Comparator.comparing(Task::getStartTime));
        System.out.println("Task added successfully. No conflicts.");
    }

    public void removeTask(String description) {
        boolean removed = tasks.removeIf(t -> t.getDescription().equalsIgnoreCase(description));
        if (removed) {
            System.out.println("Task removed successfully.");
        } else {
            System.out.println("Error: Task not found.");
        }
    }

    public void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks scheduled for the day.");
            return;
        }
        for (Task t : tasks) {
            System.out.println(t);
        }
    }
}

// Task Entity
class Task {
    private final String description;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String priority;

    private Task(String description, LocalTime startTime, LocalTime endTime, String priority) {
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
    }

    public static Task create(String desc, String start, String end, String priority) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return new Task(desc, LocalTime.parse(start, formatter), LocalTime.parse(end, formatter), priority);
    }

    public boolean overlapsWith(Task other) {
        return (startTime.isBefore(other.endTime) && endTime.isAfter(other.startTime));
    }

    public String getDescription() {
        return description;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return startTime + " - " + endTime + ": " + description + " [" + priority + "]";
    }
}

// Time Validator
class TimeValidator {
    public static boolean isValidTime(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime.parse(time, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
