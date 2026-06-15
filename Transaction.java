import java.time.LocalDateTime;

public class Transaction {

    private String fromAccount;
    private String toAccount;
    private double amount;
    private String type;
    private LocalDateTime time;

    public Transaction(String fromAccount,
                       String toAccount,
                       double amount,
                       String type) {

        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.type = type;
        this.time = LocalDateTime.now();
    }

    @Override
    public String toString() {

        return type +
                " | Amount: " + amount +
                " | From: " + fromAccount +
                " | To: " + toAccount +
                " | Time: " + time;
    }
} 
