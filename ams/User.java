import java.util.Objects;

public class User {
    private String username;
    private String password;
    private String email;
    private String role;

    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean authenticate(String inputUsername, String inputPassword) {
        return username.equalsIgnoreCase(inputUsername)
                && password.equals(inputPassword); // Plain-text check
    }

    public boolean matches(String username, String email) {
        return this.username.equalsIgnoreCase(username) && this.email.equalsIgnoreCase(email);
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public String toFileString() {
        return username + "," + password + "," + email + "," + role;
    }

    public static User fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 4) {
            return new User(parts[0], parts[1], parts[2], parts[3]);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equalsIgnoreCase(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username.toLowerCase());
    }
}