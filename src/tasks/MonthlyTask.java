package tasks;

import java.time.LocalDate;

public class MonthlyTask extends Task {
    public MonthlyTask(String title, String description, TaskType taskType, String taskDate) {
        super(title, description, taskType, taskDate);
    }

    @Override
    //если задача создана 29+ числа, а в следующем месяце нет такого числа, то задача переносится на последний день короткого месяца
    public boolean appearsLn(LocalDate date) {
        System.out.println(date.lengthOfMonth());
        if (getTaskDate().getDayOfMonth() > 28 && date.getDayOfMonth() == date.lengthOfMonth() && date.lengthOfMonth() < getTaskDate().getDayOfMonth()) {
            return true;
        } else {
            return getTaskDate().getDayOfMonth() == date.getDayOfMonth();
        }
    }
}
