import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankService {

    private Map<String, User> users = new HashMap<>();
    private Map<String, Account> accounts = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

    private int userCounter = 1;
    private int accountCounter = 1001;

    public boolean registerUser(String name,
            String email,
            String password) {

        if (users.containsKey(email)) {
            return false;
        }

        User user = new User(
                userCounter++,
                name,
                email,
                password);

        users.put(email, user);
        return true;
    }

    public boolean login(String email,
            String password) {

        User user = users.get(email);

        return user != null &&
                user.getPassword().equals(password);
    }

    public boolean createAccount(String email,
            Bank bank) {

        User user = users.get(email);

        if (user == null) {
            return false;
        }

        String key = email + "_" + bank.getBankName();

        if (accounts.containsKey(key)) {
            return false;
        }

        Account account = new Account(
                "ACC" + accountCounter++,
                user,
                bank);

        accounts.put(key, account);

        return true;
    }

    public Account getAccount(String email,
            String bankName) {

        return accounts.get(
                email + "_" + bankName);
    }

    public boolean deposit(String email,
            String bankName,
            double amount) {

        Account account = getAccount(email, bankName);

        if (account == null) {
            return false;
        }

        account.deposit(amount);

        transactions.add(
                new Transaction(
                        account.getAccountNumber(),
                        account.getAccountNumber(),
                        amount,
                        "DEPOSIT"));

        return true;
    }

    public boolean withdraw(String email,
            String bankName,
            double amount) {

        Account account = getAccount(email, bankName);

        if (account == null) {
            return false;
        }

        boolean success = account.withdraw(amount);

        if (success) {

            transactions.add(
                    new Transaction(
                            account.getAccountNumber(),
                            account.getAccountNumber(),
                            amount,
                            "WITHDRAW"));
        }

        return success;
    }

    public boolean transferFunds(
            String fromEmail,
            String fromBank,
            String toEmail,
            String toBank,
            double amount) {

        Account sender = getAccount(
                fromEmail,
                fromBank);

        Account receiver = getAccount(
                toEmail,
                toBank);

        if (sender == null ||
                receiver == null) {

            return false;
        }

        if (!sender.withdraw(amount)) {
            return false;
        }

        receiver.deposit(amount);

        transactions.add(
                new Transaction(
                        sender.getAccountNumber(),
                        receiver.getAccountNumber(),
                        amount,
                        "TRANSFER"));

        return true;
    }

    public double checkBalance(
            String email,
            String bankName) {

        Account account = getAccount(email, bankName);

        if (account == null) {
            return -1;
        }

        return account.getBalance();
    }

    public void showTransactions() {

        if (transactions.isEmpty()) {

            System.out.println(
                    "No Transactions Found");

            return;
        }

        for (Transaction t : transactions) {

            System.out.println(t);
        }
    }
}
