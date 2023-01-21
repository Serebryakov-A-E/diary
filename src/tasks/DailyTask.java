package tasks;

import java.time.LocalDate;

public class DailyTask extends Task {
    public DailyTask(String title, String description, TaskType taskType, String taskDate) {
        super(title, description, taskType,taskDate);
    }

    @Override
    public boolean appearsLn(LocalDate date) {
        return true;
    }
}
