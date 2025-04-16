public class Accommodation {
    private int id;
    private String location;
    private double price;
    private boolean available;
    private String adminUsername;
    private String adminPhone;
    private String bookedBy;

    public Accommodation(int id, String location, double price, boolean available,
                         String adminUsername, String adminPhone) {
        this.id = id;
        this.location = location;
        this.price = price;
        this.available = available;
        this.adminUsername = adminUsername;
        this.adminPhone = adminPhone;
        this.bookedBy = null;
    }

    public int getId() { return id; }
    public String getLocation() { return location; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public String getAdminUsername() { return adminUsername; }
    public String getAdminPhone() { return adminPhone; }
    public String getBookedBy() { return bookedBy; }

    public void book(String username) {
        this.available = false;
        this.bookedBy = username;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | $%.2f | Contact: %s | %s",
                id, location, price, adminPhone,
                available ? "AVAILABLE" : "BOOKED by " + bookedBy);
    }

    public String toFileString() {
        return String.join(",",
                String.valueOf(id),
                location,
                String.valueOf(price),
                String.valueOf(available),
                adminUsername,
                adminPhone,
                bookedBy != null ? bookedBy : "null"
        );
    }

    public static Accommodation fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 7) {
            Accommodation acc = new Accommodation(
                    Integer.parseInt(parts[0]),
                    parts[1],
                    Double.parseDouble(parts[2]),
                    Boolean.parseBoolean(parts[3]),
                    parts[4],
                    parts[5]
            );
            if (!parts[6].equals("null")) {
                acc.book(parts[6]);
            }
            return acc;
        }
        return null;
    }
}