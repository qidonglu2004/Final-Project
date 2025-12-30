public class TeamMember {
    private final String memberId;
    private final String name;
    private final String role;

    public TeamMember(String memberId, String name, String role) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("memberId cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        this.memberId = memberId.trim();
        this.name = name.trim();
        this.role = role == null ? "" : role.trim();
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}

