import java.io.*;
import java.util.*;

public class UserManager {
    private List<User> users = new ArrayList<>();
    private String filePath;

    public UserManager(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
        loadUsers();
    }

    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error creating users file: " + e.getMessage());
        }
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromFileString(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load users: " + e.getMessage());
        }
    }

    public boolean authenticate(String username, String password) {
        String hashedInput = PasswordUtils.hashPassword(password);
        return users.stream()
                .anyMatch(user -> user.authenticate(username, hashedInput));
    }

    public boolean usernameExists(String username) {
        return users.stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }

    public void addUser(User newUser) {
        if (!usernameExists(newUser.getUsername())) {
            users.add(newUser);
            saveUsers();
        }
    }

    public String getUserRole(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .map(User::getRole)
                .orElse(null);
    }

    public boolean resetPassword(String username, String email, String newPassword) {
        for (User user : users) {
            if (user.matches(username, email)) {
                user.setPassword(newPassword); // Stores plain text
                saveUsers();
                return true;
            }
        }
        return false;
    }

    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (User user : users) {
                writer.println(user.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
}