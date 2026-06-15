public class Account {

    private String accountNumber;
    private User user;
    private Bank bank;
    private double balance;

    public Account(String accountNumber,
                   User user,
                   Bank bank) {

        this.accountNumber = accountNumber;
        this.user = user;
        this.bank = bank;
        this.balance = 0;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Bank getBank() {
        return bank;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {

        if(balance >= amount) {
            balance -= amount;
            return true;
        }

        return false;
    }
}