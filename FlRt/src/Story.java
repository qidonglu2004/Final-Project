import java.util.ArrayList;
import java.util.List;

public class Story extends Task {

    private final List<SubTask> subTasks;

    public Story(String id, String title, String description) {
        super(id, title, description);
        this.subTasks = new ArrayList<>();
    }

    public void addSubTask(SubTask task) {
        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }
        if (task.getParentStory() != this) {
            throw new IllegalArgumentException("SubTask must belong to this Story");
        }
        subTasks.add(task);
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public double getProgress() {
        if (subTasks.isEmpty()) {
            return 0.0;
        }
        long doneCount = subTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        return (double) doneCount / subTasks.size();
    }
}