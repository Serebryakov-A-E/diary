import tasks.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class TaskService {
    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final ArrayList<Task> removedTasks = new ArrayList<>();
    private static int taskId;
    private static TaskService INSTANCE;

    private TaskService() {
        taskId = 0;
        taskMap.put(++taskId, new DailyTask("Позавтракать", "test", TaskType.PERSONAL, "2023-01-14T08:30"));
        taskMap.put(++taskId, new DailyTask("Почистить зубы", "", TaskType.PERSONAL, "2023-01-14T08:00"));
        taskMap.put(++taskId, new WeeklyTask("Тренировка", "Фулбоди", TaskType.PERSONAL, "2023-01-15T10:00"));
        taskMap.put(++taskId, new WeeklyTask("Тренировка", "Фулбоди", TaskType.PERSONAL, "2023-01-13T10:00"));
        taskMap.put(++taskId, new WeeklyTask("Тренировка", "Фулбоди", TaskType.PERSONAL, "2023-01-17T10:00"));
        taskMap.put(++taskId, new DailyTask("Работа", "", TaskType.WORK, "2023-01-14T09:00"));
        taskMap.put(++taskId, new MonthlyTask("Ревизия", "Описание.", TaskType.WORK, "2023-01-20T00:00"));
    }

    public static TaskService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TaskService();
        }
        return INSTANCE;
    }

    public void openMenu() {
        try (Scanner scanner = new Scanner(System.in)) {
            label:
            while (true) {
                printMenu();
                System.out.print("Выберите пункт меню: ");
                if (scanner.hasNextInt()) {
                    int menu = scanner.nextInt();
                    switch (menu) {
                        case 1:
                            // todo: обрабатываем пункт меню 3
                            inputTask(scanner);
                            break;
                        case 2:
                            deleteTask(scanner);
                            break;
                        case 3:
                            getTasks(scanner);
                            break;
                        case 4:
                            getDeletedTasks();
                            break;
                        case 5:
                            getAllTasksByDates();
                            break;
                        case 6:
                            changeTask(scanner);
                            break;
                        case 0:
                            break label;
                    }
                } else {
                    scanner.next();
                    System.out.println("Выберите пункт меню из списка!");
                }
            }
        }
    }

    private void inputTask(Scanner scanner) {
        System.out.print("Введите название задачи: ");
        String taskName = scanner.next() + scanner.nextLine();

        System.out.print("Введите описание задачи: ");
        String taskDescription = scanner.nextLine();

        //проверка типа задачи
        TaskType taskType;
        while (true) {
            System.out.print("Выберите тип задачи (рабочая/личная): ");
            String taskT = scanner.nextLine();
            try {
                checkTaskType(taskT);
                taskType = TaskType.getByTypeName(taskT);
                break;
            } catch (IncorrectArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        //проверка даты и времени
        String taskDate;
        String taskTime;
        while (true) {
            System.out.print("Введите дату задачи в формате yyyy-MM-dd(если дата задачи сегодня, то введите - 0): ");
            String taskDateInput = scanner.nextLine();
            if (taskDateInput.equals("0")) {
                taskDate = LocalDate.now().toString();
            } else {
                taskDate = taskDateInput;
            }

            System.out.print("Введите время задачи в формате HH:mm(если задача не привязана ко времени введите - 0): ");
            String taskInputTime = scanner.nextLine();
            if (taskInputTime.equals("0")) {
                taskTime = "00:00";
            } else {
                taskTime = taskInputTime;
            }

            try {
                checkDateFormat(taskDate, taskTime);
                break;
            } catch (IncorrectArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        //выбор повторяемости задачи
        taskId++;
        while (true) {
            System.out.print("\n1. однократная,\n" +
                    "2. ежедневная,\n" +
                    "3. еженедельная,\n" +
                    "4. ежемесячная,\n" +
                    "5. ежегодная. \n" +
                    "Выберите повторяемость задачи: ");
            String taskRepeat = scanner.nextLine();
            switch (taskRepeat) {
                case "1":
                    taskMap.put(taskId, new OneTimeTask(taskName, taskDescription, taskType, convertDate(taskDate, taskTime)));
                    break;
                case "2":
                    taskMap.put(taskId, new DailyTask(taskName, taskDescription, taskType, convertDate(taskDate, taskTime)));
                    break;
                case "3":
                    taskMap.put(taskId, new WeeklyTask(taskName, taskDescription, taskType, convertDate(taskDate, taskTime)));
                    break;
                case "4":
                    taskMap.put(taskId, new MonthlyTask(taskName, taskDescription, taskType, convertDate(taskDate, taskTime)));
                    break;
                case "5":
                    taskMap.put(taskId, new YearlyTask(taskName, taskDescription, taskType, convertDate(taskDate, taskTime)));
                    break;
                default:
                    System.out.println("Выбрана неверная повторяемость задачи. Попробуйте снова.");
                    continue;
            }
            break;
        }
        System.out.println("Задача успешно добавлена.\n");
    }

    private void getAllTasksByDates() {
        System.out.println();
        HashMap<LocalDate, ArrayList<Task>> dateMap = new HashMap<>();

        taskMap.forEach((id, task) -> {
            if (dateMap.containsKey(task.getTaskDate().toLocalDate())) {
                dateMap.get(task.getTaskDate().toLocalDate()).add(task);
            } else {
                dateMap.put(task.getTaskDate().toLocalDate(), new ArrayList<>(List.of(task)));
            }
        });

        List<LocalDate> dateList = new ArrayList<>(dateMap.keySet());
        Collections.sort(dateList);

        for (LocalDate date : dateList) {
            System.out.println("Задачи на " + date + ":");
            dateMap.get(date).sort(new TimeComparator());
            dateMap.get(date).forEach(System.out::println);
            System.out.println();
        }
    }

    private void getDeletedTasks() {
        System.out.println();
        if (removedTasks.isEmpty()) {
            System.out.println("Список удалённых задач пуст.");
        } else {
            System.out.println("Удалённые задачи:");
            removedTasks.forEach(System.out::println);
        }
        System.out.println();
    }

    private void deleteTask(Scanner scanner) {
        while (true) {
            System.out.print("Введите id задачи, котрую хотите удалить (back, чтобы вернуться назад): ");
            String id = scanner.next();
            if (id.equals("back")) {
                break;
            } else {
                try {
                    if (!taskMap.containsKey(Integer.parseInt(id))) {
                        System.out.println("Задачи с таким Id нет.");
                    } else {
                        removedTasks.add(taskMap.get(Integer.parseInt(id)));
                        taskMap.remove(Integer.parseInt(id));
                        System.out.println("Задача удалена.");
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Неверный формат ввода. Id задачи - цифра.");
                }
            }
        }
        System.out.println();
    }

    private void changeTask(Scanner scanner) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        label:
        while (true) {
            System.out.println("\n1. Показать список всех задач.\n" + "2. Выбрать задачу для изменения\n" + "back - вернуться назад");
            System.out.print("Выберите пункт меню: ");
            String input = scanner.next() + scanner.nextLine();
            switch (input) {
                case "1":
                    System.out.println("Задачи: ");
                    taskMap.forEach((integer, task) -> System.out.printf("id%d, %s %s\n", integer, task.getTitle(), task.getTaskDate().format(formatter)));
                    break;
                case "2":
                    System.out.print("Введите id задачи: ");
                    try {
                        int id = Integer.parseInt(scanner.nextLine());
                        if (!taskMap.containsKey(id)) {
                            System.out.println("Задачи с таким id нет.");
                            break;
                        } else {
                            System.out.println("Введите новый заголовок задачи");
                            String title = scanner.nextLine();
                            System.out.println("Введите новое описание задачи");
                            String description = scanner.nextLine();
                            taskMap.get(id).setTitle(title);
                            taskMap.get(id).setDescription(description);
                            System.out.println("Задача была изменена успешно.\n");
                        }
                        break label;
                    } catch (NumberFormatException e) {
                        System.out.println("Неверный формат ввода.");
                    }
                    break;
                case "back":
                    System.out.println();
                    break label;
                default:
                    System.out.println("Неверный формат ввода.");
                    break;
            }
        }
    }

    private void getTasks(Scanner scanner) {
        ArrayList<Task> taskArr = new ArrayList<>();
        LocalDate date;

        while (true) {
            System.out.print("Введите дату в формате yyyy-MM-dd (получить задачи на сегодня - 0): ");
            String input = scanner.next();

            if (input.equals("0")) {
                date = LocalDate.now();
                break;
            } else {
                try {
                    date = LocalDate.parse(input);
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Неверный формат ввода. Попробуйте ещё раз.");
                }
            }
        }

        LocalDate finalDate = date;

        System.out.println("\nСписок задач на " + date.getDayOfWeek() + " " + date);
        taskMap.forEach((id, task) -> {
            if (task.appearsLn(finalDate)) {
                taskArr.add(task);
            }
        });

        taskArr.sort(new TimeComparator());
        int counter = 1;
        for (Task t : taskArr) {
            System.out.println(counter++ + ". " + t);
        }
        System.out.println();
    }

    private void printMenu() {
        System.out.println("1. Добавить задачу\n2. Удалить задачу\n3. Получить задачи на указанный день\n" +
                "4. Получить все удалённые задачи\n5. Получить все задачи, сгруппированые по датам\n6. Изменить задачу\n0. Выход");
    }

    private String convertDate(String taskDate, String taskTime) {
        //2007-12-03T10:15:30 example
        return taskDate + "T" + taskTime + ":00";
    }

    private void checkTaskType(String input) throws IncorrectArgumentException {
        if (!input.equals("рабочая") && !input.equals("личная")) {
            throw new IncorrectArgumentException("Введен неверный тип задачи. Попробуйте снова.");
        }
    }

    private void checkDateFormat(String date, String time) throws IncorrectArgumentException {
        String fullDate = convertDate(date, time);
        try {
            LocalDateTime.parse(fullDate);
        } catch (DateTimeParseException ignore) {
            throw new IncorrectArgumentException("Неверный формат ввода даты. Попробуйте снова.");
        }
    }
}

class TimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        int res = o1.getTaskDate().getHour() - o2.getTaskDate().getHour();
        if (res == 0) {
            res = o1.getTaskDate().getMinute() - o2.getTaskDate().getMinute();
        }
        return res;
    }
}

/*
class DateComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        return o1.getTaskDate().compareTo(o2.getTaskDate());
    }
}

 */