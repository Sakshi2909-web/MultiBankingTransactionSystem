import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankService bankService =
                new BankService();

        Bank sbi =
                new Bank(1, "SBI");

        Bank hdfc =
                new Bank(2, "HDFC");

        Bank icici =
                new Bank(3, "ICICI");

        while (true) {

            System.out.println(
                    "\n===== MULTI BANKING SYSTEM ====="
            );

            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:

                    System.out.print(
                            "Enter Name: "
                    );
                    String name =
                            sc.nextLine();

                    System.out.print(
                            "Enter Email: "
                    );
                    String email =
                            sc.nextLine();

                    System.out.print(
                            "Enter Password: "
                    );
                    String password =
                            sc.nextLine();

                    boolean registered =
                            bankService.registerUser(
                                    name,
                                    email,
                                    password
                            );

                    if (registered) {
                        System.out.println(
                                "Registration Successful"
                        );
                    } else {
                        System.out.println(
                                "Email Already Exists"
                        );
                    }

                    break;

                case 2:

                    System.out.print(
                            "Enter Email: "
                    );
                    email = sc.nextLine();

                    System.out.print(
                            "Enter Password: "
                    );
                    password = sc.nextLine();

                    boolean login =
                            bankService.login(
                                    email,
                                    password
                            );

                    if (!login) {

                        System.out.println(
                                "Invalid Credentials"
                        );

                        break;
                    }

                    while (true) {

                        System.out.println(
                                "\n===== USER MENU ====="
                        );

                        System.out.println(
                                "1. Create SBI Account"
                        );

                        System.out.println(
                                "2. Create HDFC Account"
                        );

                        System.out.println(
                                "3. Create ICICI Account"
                        );

                        System.out.println(
                                "4. Deposit"
                        );

                        System.out.println(
                                "5. Withdraw"
                        );

                        System.out.println(
                                "6. Check Balance"
                        );

                        System.out.println(
                                "7. Transfer Funds"
                        );

                        System.out.println(
                                "8. Transaction History"
                        );

                        System.out.println(
                                "9. Logout"
                        );

                        int option =
                                sc.nextInt();

                        sc.nextLine();

                        if (option == 1) {

                            bankService.createAccount(
                                    email,
                                    sbi
                            );

                            System.out.println(
                                    "SBI Account Created"
                            );
                        }

                        else if (option == 2) {

                            bankService.createAccount(
                                    email,
                                    hdfc
                            );

                            System.out.println(
                                    "HDFC Account Created"
                            );
                        }

                        else if (option == 3) {

                            bankService.createAccount(
                                    email,
                                    icici
                            );

                            System.out.println(
                                    "ICICI Account Created"
                            );
                        }

                        else if (option == 4) {

                            System.out.print(
                                    "Bank Name: "
                            );
                            String bank =
                                    sc.nextLine();

                            System.out.print(
                                    "Amount: "
                            );
                            double amount =
                                    sc.nextDouble();

                            bankService.deposit(
                                    email,
                                    bank,
                                    amount
                            );

                            System.out.println(
                                    "Deposit Successful"
                            );
                        }

                        else if (option == 5) {

                            System.out.print(
                                    "Bank Name: "
                            );
                            String bank =
                                    sc.nextLine();

                            System.out.print(
                                    "Amount: "
                            );
                            double amount =
                                    sc.nextDouble();

                            boolean success =
                                    bankService.withdraw(
                                            email,
                                            bank,
                                            amount
                                    );

                            if (success) {
                                System.out.println(
                                        "Withdraw Successful"
                                );
                            } else {
                                System.out.println(
                                        "Insufficient Balance"
                                );
                            }
                        }

                        else if (option == 6) {

                            System.out.print(
                                    "Bank Name: "
                            );

                            String bank =
                                    sc.nextLine();

                            double balance =
                                    bankService.checkBalance(
                                            email,
                                            bank
                                    );

                            System.out.println(
                                    "Balance = ₹" +
                                    balance
                            );
                        }

                        else if (option == 7) {

                            System.out.print(
                                    "From Bank: "
                            );
                            String fromBank =
                                    sc.nextLine();

                            System.out.print(
                                    "Receiver Email: "
                            );
                            String receiverEmail =
                                    sc.nextLine();

                            System.out.print(
                                    "Receiver Bank: "
                            );
                            String receiverBank =
                                    sc.nextLine();

                            System.out.print(
                                    "Amount: "
                            );
                            double amount =
                                    sc.nextDouble();

                            boolean transferred =
                                    bankService.transferFunds(
                                            email,
                                            fromBank,
                                            receiverEmail,
                                            receiverBank,
                                            amount
                                    );

                            if (transferred) {
                                System.out.println(
                                        "Transfer Successful"
                                );
                            } else {
                                System.out.println(
                                        "Transfer Failed"
                                );
                            }
                        }

                        else if (option == 8) {

                            bankService.showTransactions();
                        }

                        else if (option == 9) {

                            break;
                        }
                    }

                    break;

                case 3:

                    System.out.println(
                            "Thank You"
                    );

                    System.exit(0);

                default:

                    System.out.println(
                            "Invalid Choice"
                    );
            }
        }
    }
}