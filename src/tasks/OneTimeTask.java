package tasks;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OneTimeTask extends Task {

    public OneTimeTask(String title, String description, TaskType taskType, String taskDate) {
        super(title, description, taskType, taskDate);
    }

    @Override
    public boolean appearsLn(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(getTaskDate().format(formatter)).equals(date);
    }
}
