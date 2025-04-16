import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class AccommodationManager {
    private List<Accommodation> accommodations = new ArrayList<>();
    private String filePath;

    public AccommodationManager(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
        loadAccommodations();
    }

    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error creating accommodations file: " + e.getMessage());
        }
    }

    private void loadAccommodations() {
        accommodations.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Accommodation acc = Accommodation.fromFileString(line);
                if (acc != null) {
                    accommodations.add(acc);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load accommodations: " + e.getMessage());
        }
    }

    public void addAccommodation(Accommodation acc) {
        accommodations.add(acc);
        saveAccommodations();
    }

    public void displayAll() {
        if (accommodations.isEmpty()) {
            System.out.println("No accommodations available.");
            return;
        }
        System.out.println("\n--- All Properties ---");
        accommodations.forEach(System.out::println);
    }

    public void bookById(int id, String username) {
        for (Accommodation acc : accommodations) {
            if (acc.getId() == id) {
                if (acc.isAvailable()) {
                    acc.book(username);
                    saveAccommodations();
                    System.out.println("Booking successful!");
                } else {
                    System.out.println("Property already booked by " + acc.getBookedBy());
                }
                return;
            }
        }
        System.out.println("Property not found!");
    }

    public List<Accommodation> getPropertiesByAdmin(String adminUsername) {
        return accommodations.stream()
                .filter(acc -> acc.getAdminUsername().equalsIgnoreCase(adminUsername))
                .collect(Collectors.toList());
    }

    public boolean deleteProperty(int id, String adminUsername) {
        Optional<Accommodation> acc = accommodations.stream()
                .filter(a -> a.getId() == id && a.getAdminUsername().equalsIgnoreCase(adminUsername))
                .findFirst();

        if (acc.isPresent()) {
            accommodations.remove(acc.get());
            saveAccommodations();
            return true;
        }
        return false;
    }

    private void saveAccommodations() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Accommodation acc : accommodations) {
                writer.println(acc.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving accommodations: " + e.getMessage());
        }
    }

    public int getNextId() {
        return accommodations.stream()
                .mapToInt(Accommodation::getId)
                .max()
                .orElse(0) + 1;
    }
}