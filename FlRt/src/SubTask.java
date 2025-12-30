public class SubTask extends Task {
    private final Story parentStory;

    public SubTask(String id, String title, String description, Story parentStory) {
        super(id, title, description);
        if (parentStory == null) {
            throw new IllegalArgumentException("parent story cannot be null");
        }
        this.parentStory = parentStory;
    }

    public Story getParentStory() {
        return parentStory;
    }
}

