public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    IN_REVIEW,
    BLOCKED,
    DONE;

    public static TaskStatus fromString(String s) {
        if (s == null) return null;
        s = s.trim().toUpperCase();

        if ("TODO".equals(s)) return TODO;
        if ("IN_PROGRESS".equals(s)) return IN_PROGRESS;
        if ("IN_REVIEW".equals(s)) return IN_REVIEW;
        if ("BLOCKED".equals(s)) return BLOCKED;
        if ("DONE".equals(s)) return DONE;

        return null;
    }
}
