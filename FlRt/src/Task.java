import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象任務類別，符合 system_desc_spec.txt 要求。
 */
public abstract class Task {

    private final String id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private TeamMember assignee;
    private final List<StatusChange> statusHistory;
    private final List<TaskObserver> observers;

    public Task(String id, String title, String description) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("title cannot be null or empty");
        }
        this.id = id.trim();
        this.title = title.trim();
        this.description = description == null ? "" : description.trim();
        this.status = TaskStatus.TODO;
        this.priority = Priority.MEDIUM;
        this.assignee = null;
        this.statusHistory = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public TeamMember getAssignee() {
        return assignee;
    }

    public List<StatusChange> getStatusHistory() {
        return new ArrayList<>(statusHistory);
    }

    /**
     * 方便既有呼叫使用的版本，視為「系統」或未知操作者。
     * 如需正確記錄操作者，請優先使用 {@link #transitionTo(TaskStatus, TeamMember)}。
     */
    public boolean transitionTo(TaskStatus newStatus) {
        return transitionTo(newStatus, null);
    }

    /**
     * 狀態轉換，並記錄實際進行操作的成員。
     */
    public boolean transitionTo(TaskStatus newStatus, TeamMember changedBy) {
        // 規格僅定義特定合法轉換，對於「同一狀態」不視為特例，一律當作非法轉換處理
        if (this.status == newStatus) {
            throw new IllegalStateException("Cannot transition from " + this.status + " to " + newStatus);
        }

        if (!isLegalTransition(this.status, newStatus)) {
            throw new IllegalStateException("Cannot transition from " + this.status + " to " + newStatus);
        }

        TaskStatus fromStatus = this.status;
        this.status = newStatus;

        StatusChange change = new StatusChange(fromStatus, newStatus, changedBy, Instant.now().toString());
        statusHistory.add(change);
        notifyObservers();
        return true;
    }

    public void assignTo(TeamMember member) {
        if (member == null) {
            throw new IllegalArgumentException("member cannot be null");
        }
        this.assignee = member;
    }

    public void unassign() {
        this.assignee = null;
    }

    public void setPriority(Priority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("priority cannot be null");
        }
        this.priority = priority;
    }

    public void setTitle(String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("title cannot be null or empty");
        }
        this.title = newTitle.trim();
    }

    public void setDescription(String newDescription) {
        this.description = newDescription == null ? "" : newDescription.trim();
    }

    public void addObserver(TaskObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(TaskObserver observer) {
        observers.remove(observer);
    }

    /**
     * 規格所定義的無參數 notifyObservers()。
     * 會以目前最新一筆 StatusChange 做為通知內容。
     */
    protected void notifyObservers() {
        if (statusHistory.isEmpty()) {
            return;
        }
        StatusChange change = statusHistory.get(statusHistory.size() - 1);
        for (TaskObserver observer : observers) {
            observer.onStatusChanged(this, change);
        }
    }

    protected boolean isLegalTransition(TaskStatus from, TaskStatus to) {
        if (from == TaskStatus.TODO && to == TaskStatus.IN_PROGRESS) return true;
        if (from == TaskStatus.IN_PROGRESS && (to == TaskStatus.IN_REVIEW || to == TaskStatus.BLOCKED)) return true;
        if (from == TaskStatus.IN_REVIEW && (to == TaskStatus.DONE || to == TaskStatus.IN_PROGRESS)) return true;
        if (from == TaskStatus.BLOCKED && to == TaskStatus.IN_PROGRESS) return true;
        return from == TaskStatus.DONE && to == TaskStatus.IN_PROGRESS;
    }
}
