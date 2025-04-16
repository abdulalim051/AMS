public class PasswordUtils {
    // No hashing - stores passwords as-is
    public static String hashPassword(String password) {
        return password; // Returns plain text
    }

    // Direct comparison (no hashing)
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        return inputPassword.equals(storedPassword);
    }
}