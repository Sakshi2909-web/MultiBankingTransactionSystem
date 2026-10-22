package com.banking;

import com.banking.dao.BankDAO;
import com.banking.dao.BankService;
import com.banking.model.Bank;
import com.banking.model.Transaction;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        BankService bankService = new BankService();
        BankDAO bankDAO = new BankDAO();

        Bank sbi = bankDAO.getBankByName("SBI");
        if (sbi == null) sbi = new Bank(1, "SBI", "SBI", "SBIN0001");

        Bank hdfc = bankDAO.getBankByName("HDFC");
        if (hdfc == null) hdfc = new Bank(2, "HDFC", "HDFC", "HDFC0002");

        Bank icici = bankDAO.getBankByName("ICICI");
        if (icici == null) icici = new Bank(3, "ICICI", "ICICI", "ICIC0003");

        while (true) {

            System.out.println("\n========================================");
            System.out.println("   🏦 MULTI BANKING TRANSACTION SYSTEM   ");
            System.out.println("========================================");
            System.out.println("1. Register New User");
            System.out.println("2. User Login");
            System.out.println("3. Exit System");
            System.out.print("Enter Choice (1-3): ");

            if (!sc.hasNextInt()) {
                sc.nextLine();
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.println("\n--- USER REGISTRATION ---");
                    System.out.print("Enter Full Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Email Address: ");
                    String email = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String password = sc.nextLine();

                    boolean registered = bankService.registerUser(name, email, password);

                    if (registered) {
                        System.out.println("✅ Registration Successful! You can now log in.");
                    } else {
                        System.out.println("❌ Email already exists or database error!");
                    }
                    break;

                case 2:
                    System.out.println("\n--- USER LOGIN ---");
                    System.out.print("Enter Email: ");
                    email = sc.nextLine();

                    System.out.print("Enter Password: ");
                    password = sc.nextLine();

                    boolean login = bankService.login(email, password);

                    if (!login) {
                        System.out.println("❌ Invalid Credentials! Please check your email/password.");
                        break;
                    }

                    System.out.println("✅ Login Successful! Welcome " + email);

                    userLoop:
                    while (true) {
                        System.out.println("\n==============================");
                        System.out.println("       USER DASHBOARD MENU    ");
                        System.out.println("==============================");
                        System.out.println("1. Create SBI Account");
                        System.out.println("2. Create HDFC Account");
                        System.out.println("3. Create ICICI Account");
                        System.out.println("4. Deposit Money");
                        System.out.println("5. Withdraw Money");
                        System.out.println("6. Check Balance");
                        System.out.println("7. Transfer Funds");
                        System.out.println("8. Transaction History / Passbook");
                        System.out.println("9. Logout");
                        System.out.print("Select Option (1-9): ");

                        if (!sc.hasNextInt()) {
                            sc.nextLine();
                            System.out.println("Invalid input!");
                            continue;
                        }

                        int option = sc.nextInt();
                        sc.nextLine();

                        switch (option) {
                            case 1:
                                if (bankService.createAccount(email, sbi)) {
                                    System.out.println("✅ SBI Account Created Successfully!");
                                } else {
                                    System.out.println("⚠️ SBI Account Already Exists for this user!");
                                }
                                break;

                            case 2:
                                if (bankService.createAccount(email, hdfc)) {
                                    System.out.println("✅ HDFC Account Created Successfully!");
                                } else {
                                    System.out.println("⚠️ HDFC Account Already Exists for this user!");
                                }
                                break;

                            case 3:
                                if (bankService.createAccount(email, icici)) {
                                    System.out.println("✅ ICICI Account Created Successfully!");
                                } else {
                                    System.out.println("⚠️ ICICI Account Already Exists for this user!");
                                }
                                break;

                            case 4:
                                System.out.print("Bank Name (SBI / HDFC / ICICI): ");
                                String bank = sc.nextLine();
                                System.out.print("Enter Amount to Deposit: ₹");
                                double depAmount = sc.nextDouble();
                                sc.nextLine();

                                if (bankService.deposit(email, bank, depAmount)) {
                                    System.out.println("✅ Deposit of ₹" + depAmount + " Successful!");
                                } else {
                                    System.out.println("❌ Deposit Failed! Make sure account exists in " + bank);
                                }
                                break;

                            case 5:
                                System.out.print("Bank Name (SBI / HDFC / ICICI): ");
                                bank = sc.nextLine();
                                System.out.print("Enter Amount to Withdraw: ₹");
                                double withAmount = sc.nextDouble();
                                sc.nextLine();

                                if (bankService.withdraw(email, bank, withAmount)) {
                                    System.out.println("✅ Withdrawal of ₹" + withAmount + " Successful!");
                                } else {
                                    System.out.println("❌ Withdrawal Failed! Check account balance or bank name.");
                                }
                                break;

                            case 6:
                                System.out.print("Bank Name (SBI / HDFC / ICICI): ");
                                bank = sc.nextLine();
                                double bal = bankService.checkBalance(email, bank);
                                if (bal >= 0) {
                                    System.out.println("💰 Available Balance in " + bank.toUpperCase() + " = ₹" + bal);
                                } else {
                                    System.out.println("❌ Account not found in " + bank);
                                }
                                break;

                            case 7:
                                System.out.print("Sender Bank (SBI / HDFC / ICICI): ");
                                String fromBank = sc.nextLine();
                                System.out.print("Receiver Email: ");
                                String receiverEmail = sc.nextLine();
                                System.out.print("Receiver Bank (SBI / HDFC / ICICI): ");
                                String receiverBank = sc.nextLine();
                                System.out.print("Transfer Amount: ₹");
                                double trfAmount = sc.nextDouble();
                                sc.nextLine();

                                boolean transferred = bankService.transferFunds(
                                    email, fromBank, receiverEmail, receiverBank, trfAmount
                                );

                                if (transferred) {
                                    System.out.println("✅ Transfer of ₹" + trfAmount + " Successful!");
                                } else {
                                    System.out.println("❌ Transfer Failed! Verify funds, sender/receiver emails and bank names.");
                                }
                                break;

                            case 8:
                                System.out.println("\n--- RECENT TRANSACTIONS ---");
                                List<Transaction> txns = bankService.showTransactions();
                                if (txns.isEmpty()) {
                                    System.out.println("No transactions found.");
                                } else {
                                    for (Transaction t : txns) {
                                        System.out.println("• " + t);
                                    }
                                }
                                break;

                            case 9:
                                System.out.println("Logged out successfully.");
                                break userLoop;

                            default:
                                System.out.println("Invalid Option! Please enter 1-9.");
                        }
                    }
                    break;

                case 3:
                    System.out.println("\nThank you for using Multi Banking System. Goodbye!");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid Choice! Please enter 1, 2, or 3.");
            }
        }
    }
}
