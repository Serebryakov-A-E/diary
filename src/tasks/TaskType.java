package tasks;

public enum TaskType {
    PERSONAL("личная"),
    WORK("рабочая");

    private final String type;

    TaskType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public static TaskType getByTypeName(String type) {
        for (TaskType elem : values()) {
            if (elem.type.equals(type)) {
                return elem;
            }
        }
        return null;
    }
}
