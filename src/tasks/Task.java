package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Task implements Repeatable {
    private String title;
    private String description;
    private final LocalDateTime taskDate;
    private final TaskType taskType;

    public Task(String title, String description, TaskType taskType, String date) {
        this.title = title;
        this.description = description;
        this.taskDate = LocalDateTime.parse(date);
        this.taskType = taskType;
    }

    public LocalDateTime getTaskDate() {
        return taskDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return title + " " + taskDate.format(formatter) + ". " + taskType.getType() + ".";
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
