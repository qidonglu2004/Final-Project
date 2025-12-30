public class Bug extends Task {

    private Priority severity;

    public Bug(String id, String title, String description, Priority severity) {
        super(id, title, description);
        setSeverity(severity);
    }

    public void setSeverity(Priority level) {
        if (level == null) {
            throw new IllegalArgumentException("level cannot be null");
        }
        this.severity = level;
    }

    public Priority getSeverity() {
        return severity;
    }
}