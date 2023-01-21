package tasks;

import java.time.LocalDate;

public class WeeklyTask extends Task {

    public WeeklyTask(String title, String description, TaskType taskType, String taskDate) {
        super(title, description, taskType, taskDate);
    }

    @Override
    public boolean appearsLn(LocalDate date) {
        return getTaskDate().getDayOfWeek() == date.getDayOfWeek();
    }
}
