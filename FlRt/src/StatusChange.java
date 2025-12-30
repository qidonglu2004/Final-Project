public class StatusChange {
    private final TaskStatus fromStatus;
    private final TaskStatus toStatus;
    private final TeamMember changedBy;
    private final String timestamp;

    public StatusChange(TaskStatus fromStatus, TaskStatus toStatus, TeamMember changedBy, String timestamp) {
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.timestamp = timestamp;
    }

    public TaskStatus getFromStatus() {
        return fromStatus;
    }

    public TaskStatus getToStatus() {
        return toStatus;
    }

    public TeamMember getChangedBy() {
        return changedBy;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        String actor = changedBy == null ? "system" : changedBy.getName();
        return timestamp + " : " + fromStatus + " -> " + toStatus + " by " + actor;
    }
}
