import java.time.LocalDateTime;

public class Payment {
    private String paymentId;
    private String username;
    private int accommodationId;
    private double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private boolean isPaid;

    public Payment(String paymentId, String username, int accommodationId,
                   double amount, String paymentMethod) {
        this.paymentId = paymentId;
        this.username = username;
        this.accommodationId = accommodationId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.isPaid = false;
    }

    // Getters and Setters
    public void processPayment() {
        this.isPaid = true;
        this.paymentDate = LocalDateTime.now();
    }

    public String toFileString() {
        return String.join(",",
                paymentId,
                username,
                String.valueOf(accommodationId),
                String.valueOf(amount),
                paymentDate.toString(),
                paymentMethod,
                String.valueOf(isPaid)
        );
    }

    public static Payment fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 7) {
            Payment payment = new Payment(
                    parts[0], parts[1], Integer.parseInt(parts[2]),
                    Double.parseDouble(parts[3]), parts[5]
            );
            payment.paymentDate = LocalDateTime.parse(parts[4]);
            payment.isPaid = Boolean.parseBoolean(parts[6]);
            return payment;
        }
        return null;
    }
}