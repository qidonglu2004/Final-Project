import java.util.ArrayList;
import java.util.List;

public class Project {

    private final String name;
    private final List<Task> tasks;
    private final List<TeamMember> members;

    public Project(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }
        tasks.add(task);
    }

    public void addMember(TeamMember member) {
        if (member == null) {
            throw new IllegalArgumentException("member cannot be null");
        }
        boolean exists = members.stream().anyMatch(m -> m.getMemberId().equals(member.getMemberId()));
        if (exists) {
            throw new IllegalArgumentException("member already exists");
        }
        members.add(member);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public List<TeamMember> getMembers() {
        return new ArrayList<>(members);
    }
}