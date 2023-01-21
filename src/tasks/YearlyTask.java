package tasks;

import java.time.LocalDate;

public class YearlyTask extends Task {
    public YearlyTask(String title, String description, TaskType taskType, String taskDate) {
        super(title, description, taskType, taskDate);
    }

    @Override
    public boolean appearsLn(LocalDate date) {
        return (getTaskDate().getDayOfYear() == date.getDayOfYear());
    }
}
