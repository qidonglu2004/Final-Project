import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Story> stories;

    public Epic(String id, String title, String description) {
        super(id, title, description);
        this.stories = new ArrayList<>();
    }

    public void addStory(Story story) {
        if (story == null) {
            throw new IllegalArgumentException("story cannot be null");
        }
        stories.add(story);
    }

    public List<Story> getStories() {
        return new ArrayList<>(stories);
    }

    public double getCompletionRate() {
        if (stories.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Story story : stories) {
            sum += story.getProgress();
        }
        return sum / stories.size();
    }
}

