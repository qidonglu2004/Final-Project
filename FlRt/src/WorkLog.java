public class WorkLog {
    private final TeamMember member;
    private final int hours;
    private final String description;

    public WorkLog(TeamMember member, int hours, String description) {
        this.member = member;
        this.hours = hours;
        this.description = description;
    }

    public TeamMember getMember() {
        return member;
    }

    public int getHours() {
        return hours;
    }

    public String getDescription() {
        return description;
    }
}