import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Board {
    private final List<TaskStatus> columns;
    private final List<Task> tasks;

    public Board() {
        this.columns = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public void addColumn(TaskStatus status) {
        if (status != null && !columns.contains(status)) {
            columns.add(status);
        }
    }

    public void addTask(Task task) {
        if (task != null) {
            tasks.add(task);
        }
    }

    public List<TaskStatus> getColumns() {
        return new ArrayList<>(columns);
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return tasks.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}

