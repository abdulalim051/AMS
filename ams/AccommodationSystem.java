import java.util.Scanner;
import java.util.List;

public class AccommodationSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserManager userManager = new UserManager("users.txt");
        AccommodationManager accManager = new AccommodationManager("accommodations.txt");
        RestaurantManager resManager = new RestaurantManager("restaurants.txt");

        try {
            boolean running = true;
            while (running) {
                System.out.println("\n=== Welcome to Accommodation System ===");
                System.out.println("1. Login");
                System.out.println("2. Sign Up");
                System.out.println("3. Reset Password");
                System.out.println("4. Exit");
                System.out.print("Choose an option: ");

                int option = getIntInput(scanner, "Choose an option: ");
                scanner.nextLine(); // consume newline

                switch (option) {
                    case 1 -> running = handleLogin(scanner, userManager, accManager, resManager);
                    case 2 -> handleSignUp(scanner, userManager);
                    case 3 -> handlePasswordReset(scanner, userManager);
                    case 4 -> {
                        System.out.println("Exiting system. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        } finally {
            scanner.close();
        }
    }

    private static boolean handleLogin(Scanner scanner, UserManager userManager,
                                       AccommodationManager accManager, RestaurantManager resManager) {
        System.out.print("\nUsername: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!userManager.authenticate(username, password)) {
            System.out.println("Invalid credentials. Please try again.");
            return true;
        }

        String role = userManager.getUserRole(username);
        System.out.println("\nLogin successful! Welcome, " + username + " (" + role + ")");

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Accommodation Options");
            System.out.println("2. Restaurant Options");
            System.out.println("3. Logout");

            int choice = getIntInput(scanner, "Enter option: ");
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> {
                    if ("admin".equalsIgnoreCase(role)) {
                        handleAdminMenu(scanner, accManager, username);
                    } else {
                        handleAccommodationMenu(scanner, accManager, role, username);
                    }
                }
                case 2 -> handleRestaurantMenu(scanner, resManager, role);
                case 3 -> {
                    System.out.println("Logging out...");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
        return true;
    }

    private static void handleAdminMenu(Scanner scanner, AccommodationManager accManager,
                                        String username) {
        while (true) {
            System.out.println("\n=== Admin Dashboard ===");
            System.out.println("1. View My Properties");
            System.out.println("2. Add New Property");
            System.out.println("3. Delete Property");
            System.out.println("4. View Bookings");
            System.out.println("5. Back to Main Menu");

            int choice = getIntInput(scanner, "Choose option: ");
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.println("\n--- Your Properties ---");
                    List<Accommodation> properties = accManager.getPropertiesByAdmin(username);
                    if (properties.isEmpty()) {
                        System.out.println("You have no properties listed.");
                    } else {
                        properties.forEach(System.out::println);
                    }
                }
                case 2 -> {
                    System.out.print("Enter location: ");
                    String location = scanner.nextLine();
                    System.out.print("Enter price: ");
                    double price = getDoubleInput(scanner, "Enter price: ");
                    scanner.nextLine();
                    System.out.print("Your contact phone: ");
                    String phone = scanner.nextLine();

                    accManager.addAccommodation(new Accommodation(
                            accManager.getNextId(), location, price, true, username, phone));
                    System.out.println("Property added successfully!");
                }
                case 3 -> {
                    System.out.print("Enter property ID to delete: ");
                    int id = getIntInput(scanner, "Enter ID: ");
                    scanner.nextLine();
                    if (accManager.deleteProperty(id, username)) {
                        System.out.println("Property deleted successfully!");
                    } else {
                        System.out.println("Deletion failed. Invalid ID or not your property.");
                    }
                }
                case 4 -> {
                    System.out.println("\n--- Booking Report ---");
                    List<Accommodation> bookedProperties = accManager.getPropertiesByAdmin(username)
                            .stream()
                            .filter(acc -> !acc.isAvailable())
                            .toList();

                    if (bookedProperties.isEmpty()) {
                        System.out.println("No bookings found for your properties.");
                    } else {
                        bookedProperties.forEach(acc ->
                                System.out.printf("%s (ID: %d) booked by %s%n",
                                        acc.getLocation(), acc.getId(), acc.getBookedBy())
                        );
                    }
                }
                case 5 -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private static void handleAccommodationMenu(Scanner scanner,
                                                AccommodationManager accManager,
                                                String role, String username) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n=== Accommodation Menu ===");
            System.out.println("1. View All Accommodations");
            System.out.println("2. Book Accommodation");
            if ("admin".equalsIgnoreCase(role)) {
                System.out.println("3. Add Accommodation");
            }
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput(scanner, "Enter option: ");
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> accManager.displayAll();
                case 2 -> {
                    accManager.displayAll();
                    System.out.print("Enter ID to book: ");
                    int bookId = getIntInput(scanner, "Enter ID to book: ");
                    scanner.nextLine();
                    accManager.bookById(bookId, username);
                }
                case 3 -> {
                    if (!"admin".equalsIgnoreCase(role)) {
                        System.out.println("Only admins can add accommodations.");
                        break;
                    }
                    System.out.print("Enter location: ");
                    String location = scanner.nextLine();
                    System.out.print("Enter price: ");
                    double price = getDoubleInput(scanner, "Enter price: ");
                    scanner.nextLine();
                    System.out.print("Your contact phone: ");
                    String phone = scanner.nextLine();
                    int id = accManager.getNextId();
                    accManager.addAccommodation(new Accommodation(
                            id, location, price, true, username, phone));
                    System.out.println("Accommodation added successfully!");
                }
                case 0 -> inMenu = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void handleRestaurantMenu(Scanner scanner,
                                             RestaurantManager resManager,
                                             String role) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n=== Restaurant Menu ===");
            System.out.println("1. View Restaurants by Cuisine");
            if ("owner".equalsIgnoreCase(role)) {
                System.out.println("2. Add Restaurant");
                System.out.println("3. Delete Restaurant");
            }
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput(scanner, "Enter option: ");
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> {
                    System.out.println("Choose Cuisine Type:");
                    System.out.println("1. Bangla");
                    System.out.println("2. Chinese");
                    System.out.println("3. Intercontinental");
                    int cuisineChoice = getIntInput(scanner, "Enter choice: ");
                    scanner.nextLine();
                    String cuisine = switch (cuisineChoice) {
                        case 1 -> "Bangla";
                        case 2 -> "Chinese";
                        case 3 -> "Intercontinental";
                        default -> "Unknown";
                    };
                    resManager.displayByCuisine(cuisine);
                }
                case 2 -> {
                    if (!"owner".equalsIgnoreCase(role)) {
                        System.out.println("Only owners can add restaurants.");
                        break;
                    }
                    System.out.print("Enter restaurant name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter location: ");
                    String loc = scanner.nextLine();
                    System.out.println("Cuisine Type: 1. Bangla 2. Chinese 3. Intercontinental");
                    int cuisineChoice = getIntInput(scanner, "Enter cuisine type: ");
                    scanner.nextLine();
                    String cuisineType = switch (cuisineChoice) {
                        case 1 -> "Bangla";
                        case 2 -> "Chinese";
                        case 3 -> "Intercontinental";
                        default -> "Unknown";
                    };
                    resManager.addRestaurant(new Restaurant(name, loc, cuisineType));
                    System.out.println("Restaurant added successfully!");
                }
                case 3 -> {
                    if (!"owner".equalsIgnoreCase(role)) {
                        System.out.println("Only owners can delete restaurants.");
                        break;
                    }
                    System.out.print("Enter name of restaurant to delete: ");
                    String delName = scanner.nextLine();
                    resManager.deleteRestaurant(delName);
                }
                case 0 -> inMenu = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void handleSignUp(Scanner scanner, UserManager userManager) {
        System.out.println("\n=== Sign Up ===");
        System.out.print("Choose a username: ");
        String newUsername = scanner.nextLine();

        if (userManager.usernameExists(newUsername)) {
            System.out.println("Username already exists. Please try another.");
            return;
        }

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Choose a password: ");
        String newPassword = scanner.nextLine();

        System.out.print("Enter role (admin/guest/owner): ");
        String role = scanner.nextLine().toLowerCase();

        if (!role.matches("admin|guest|owner")) {
            System.out.println("Invalid role. Must be admin, guest, or owner.");
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        userManager.addUser(new User(newUsername, hashedPassword, email, role));
        System.out.println("Sign-up successful. You can now log in.");
    }

    private static void handlePasswordReset(Scanner scanner, UserManager userManager) {
        System.out.println("\n=== Password Reset ===");
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your registered email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your new password: ");
        String newPass = scanner.nextLine();

        if (userManager.resetPassword(username, email, newPass)) {
            System.out.println("Password reset successful. You can now login with your new password.");
        } else {
            System.out.println("Invalid username or email. Password reset failed.");
        }
    }

    private static int getIntInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear the invalid input
            }
        }
    }

    private static double getDoubleInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextDouble();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear the invalid input
            }
        }
    }
}