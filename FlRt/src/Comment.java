public class Comment {
    private final TeamMember author;
    private final String content;
    private final String createdTime;

    public Comment(TeamMember author, String content, String createdTime) {
        this.author = author;
        this.content = content;
        this.createdTime = createdTime;
    }

    public TeamMember getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedTime() {
        return createdTime;
    }
}
