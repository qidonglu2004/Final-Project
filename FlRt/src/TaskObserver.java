public interface TaskObserver {
    void onStatusChanged(Task task, StatusChange change);
}

