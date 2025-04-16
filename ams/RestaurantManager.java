import java.io.*;
import java.util.*;

public class RestaurantManager {
    private List<Restaurant> restaurants = new ArrayList<>();
    private String filePath;

    public RestaurantManager(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
        loadRestaurants();
    }

    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error creating restaurants file: " + e.getMessage());
        }
    }

    private void loadRestaurants() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Restaurant restaurant = Restaurant.fromFileString(line);
                if (restaurant != null) {
                    restaurants.add(restaurant);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load restaurants: " + e.getMessage());
        }
    }

    public void addRestaurant(Restaurant res) {
        restaurants.add(res);
        saveRestaurants();
    }

    public void deleteRestaurant(String name) {
        boolean removed = restaurants.removeIf(r -> r.getName().equalsIgnoreCase(name));
        if (removed) {
            saveRestaurants();
            System.out.println("Restaurant deleted successfully.");
        } else {
            System.out.println("Restaurant not found.");
        }
    }

    public void displayByCuisine(String cuisine) {
        List<Restaurant> filtered = restaurants.stream()
                .filter(r -> r.getCuisine().equalsIgnoreCase(cuisine))
                .toList();

        if (filtered.isEmpty()) {
            System.out.println("No restaurants found with " + cuisine + " cuisine.");
        } else {
            System.out.println("\n--- " + cuisine + " Restaurants ---");
            filtered.forEach(System.out::println);
        }
    }

    private void saveRestaurants() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Restaurant r : restaurants) {
                writer.println(r.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving restaurants: " + e.getMessage());
        }
    }
}