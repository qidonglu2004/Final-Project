import java.util.ArrayList;
import java.util.List;

/**
 * 簡單的使用者註冊 / 登入服務，只在記憶體中維護使用者清單。
 */
public class AuthService {

    private final List<User> users = new ArrayList<>();

    public AuthService() {
        // 建立一個預設帳號方便測試
        register("admin", "admin");
    }

    public User register(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("password cannot be null or empty");
        }
        String trimmed = username.trim();
        if (findByUsername(trimmed) != null) {
            throw new IllegalArgumentException("username already exists");
        }
        User user = new User(trimmed, password);
        users.add(user);
        return user;
    }

    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User user = findByUsername(username.trim());
        if (user == null) {
            return null;
        }
        return user.getPassword().equals(password) ? user : null;
    }

    private User findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}


