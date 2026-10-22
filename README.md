# 🏦 Multi Banking Transaction System (Java Dynamic Web Project)

> **A full-fledged, secure, and modern Java Dynamic Web Application for managing multiple bank accounts across partner banks with instant deposits, withdrawals, atomic fund transfers, and digital passbook statement generation.**

![Java](https://img.shields.io/badge/Java-17%20%7C%2021%20%7C%2025-orange?style=for-the-badge&logo=openjdk)
![Servlets](https://img.shields.io/badge/Java%20EE-Servlets%20%26%20JSP-red?style=for-the-badge&logo=apachetomcat)
![MySQL](https://img.shields.io/badge/MySQL-Database%208.0-blue?style=for-the-badge&logo=mysql)
![JDBC](https://img.shields.io/badge/JDBC-Prepared%20Statements-green?style=for-the-badge)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3.3-purple?style=for-the-badge&logo=bootstrap)
![Status](https://img.shields.io/badge/Status-Dynamic%20Web%20Project-success?style=for-the-badge)

---

## 🌟 Project Overview (प्रोजेक्ट का परिचय)

The **Multi Banking Transaction System** has been converted from a basic console script into an enterprise-grade **Java Dynamic Web Application (Servlets + JSP + JDBC + MySQL)**. 

Students and developers can easily open and run this project in **Eclipse IDE**, **IntelliJ IDEA**, or deploy directly onto **Apache Tomcat**. A **1-Click Local Runner (`run.bat`)** is also included so you can test it immediately without needing to install Tomcat beforehand.

### 🎯 Key Highlights
- 🏦 **Multi-Bank Linking**: Link separate accounts under SBI, HDFC, ICICI, Axis Bank, and PNB for each user.
- 💳 **Realistic ATM Card UI**: Responsive cards displaying bank gradients, chip aesthetic, account numbers, and live balances.
- 💸 **Instant Deposit & Withdrawal**: Safe transactions with real-time balance checks and ACID database commits.
- 🔄 **Atomic Inter-Bank Transfers**: Direct transfers between any two accounts or via beneficiary email and bank with automatic rollback on error.
- 📜 **Digital Passbook & Statements**: Detailed transaction audit log with filter by account, live search, and printable statements.
- ⚡ **1-Click Demo Login**: Pre-filled demo user buttons on the login screen for quick viva / college presentations.

---

## 🗂️ Project Directory Structure

```
MultiBankingTransactionSystem/
│
├── database/
│   └── database.sql                      # Complete MySQL schema & demo seed data
│
├── src/                                  # Java Source Code
│   ├── db.properties                     # Database connection configuration (URL, user, pass)
│   └── com/banking/
│       ├── model/                        # Entity Beans / Data Models
│       │   ├── User.java                 # User details bean
│       │   ├── Bank.java                 # Bank details bean (SBI, HDFC, ICICI, etc.)
│       │   ├── Account.java              # Bank Account bean
│       │   └── Transaction.java          # Transaction audit bean
│       │
│       ├── dao/                          # JDBC Data Access Layer (PreparedStatements)
│       │   ├── DBConnection.java         # Connection manager
│       │   ├── UserDAO.java              # User registration & auth queries
│       │   ├── BankDAO.java              # Bank listing queries
│       │   ├── AccountDAO.java           # Account creation & balance queries
│       │   ├── TransactionDAO.java       # Atomic transfers, deposits & passbook queries
│       │   └── BankService.java          # High-level service facade
│       │
│       └── servlet/                      # Java EE Controller Servlets
│           ├── RegisterServlet.java      # Handles /register
│           ├── LoginServlet.java         # Handles /login & session creation
│           ├── LogoutServlet.java        # Handles /logout & session invalidation
│           ├── CreateAccountServlet.java # Handles /create-account
│           ├── DepositServlet.java       # Handles /deposit
│           ├── WithdrawServlet.java      # Handles /withdraw
│           ├── TransferServlet.java      # Handles /transfer (Atomic Transaction)
│           └── TransactionHistoryServlet.java # Handles /transactions (Passbook)
│
├── WebContent/                           # Dynamic Web Root (Eclipse / Tomcat standard)
│   ├── css/
│   │   └── style.css                     # Custom banking UI theme & ATM card gradients
│   ├── js/
│   │   └── script.js                     # Live balance lookup, search filter, print
│   ├── includes/
│   │   ├── navbar.jsp                    # Dynamic session-based header navbar
│   │   ├── footer.jsp                    # Common footer & scripts
│   │   └── alerts.jsp                    # Alert toasts (success/error messages)
│   ├── WEB-INF/
│   │   ├── web.xml                       # Deployment descriptor & servlet mappings
│   │   ├── classes/                      # Compiled Java bytecode
│   │   └── lib/                          # Bundled JAR dependencies:
│   │       ├── mysql-connector-java-8.0.28.jar
│   │       ├── javax.servlet-api-4.0.1.jar
│   │       └── jstl-1.2.jar
│   ├── index.jsp                         # Homepage / Hero landing page
│   ├── login.jsp                         # Customer login with 1-click demo buttons
│   ├── register.jsp                      # New user registration
│   ├── dashboard.jsp                     # Customer dashboard & linked accounts
│   ├── create-account.jsp                # Open account in SBI/HDFC/ICICI/Axis/PNB
│   ├── deposit.jsp                       # Deposit money
│   ├── withdraw.jsp                      # Withdraw money
│   ├── transfer.jsp                      # Inter-bank money transfer
│   └── transactions.jsp                  # Passbook with search & print
│
├── runner/
│   └── LocalServer.java                  # Built-in lightweight local web runner
│
├── Main.java                             # Enhanced CLI / Console interface (JDBC connected)
├── run.bat                               # Windows 1-Click Launch Menu
├── pom.xml                               # Optional Maven configuration for IntelliJ / VS Code
├── .project                              # Eclipse project metadata
├── .classpath                            # Eclipse classpath configuration
└── .settings/                            # Eclipse WTP Dynamic Web facet settings
```

---

## 💾 Database Setup (MySQL)

1. Open **MySQL Workbench**, **phpMyAdmin (XAMPP)**, or MySQL Command Line.
2. Run the provided SQL script:
   ```bash
   mysql -u root -p < database/database.sql
   ```
   *Or open [`database/database.sql`](database/database.sql) in MySQL Workbench and execute all statements.*

3. If your MySQL root password is not `root`, simply update [`src/db.properties`](src/db.properties):
   ```properties
   db.url=jdbc:mysql://localhost:3306/multibanking?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=root
   db.password=your_mysql_password
   ```

---

## 👥 Pre-Configured Demo Credentials

Use these credentials to log in instantly (or click the demo buttons on [`login.jsp`](WebContent/login.jsp)):

| Name | Email | Password | Linked Accounts | Initial Balance |
|------|-------|----------|-----------------|-----------------|
| **Rahul Sharma** | `rahul@gmail.com` | `123456` | SBI (`SBIN1001`), HDFC (`HDFC1002`) | ₹25,000 & ₹15,000 |
| **Priya Patel** | `priya@gmail.com` | `123456` | ICICI (`ICIC2001`), SBI (`SBIN2002`) | ₹35,000 & ₹12,000 |

---

## 🚀 How to Run the Project

### Method 1: 1-Click Launcher (Easiest - No Tomcat Needed!)
1. Double-click `run.bat` (or open terminal and run `./run.bat`).
2. Choose:
   - `[1]` to launch the **Local Web Server** -> Automatically opens `http://localhost:8080/` in your browser!
   - `[2]` to run the **Console CLI Application** (`Main.java`).
   - `[3]` to recompile all classes.

---

### Method 2: In Eclipse IDE (Dynamic Web Project)
1. Open **Eclipse IDE for Enterprise Java and Web Developers**.
2. Click **File** > **Import...**
3. Select **General** > **Existing Projects into Workspace** > Click **Next**.
4. Browse and select the project folder:
   `C:\Users\...\MultiBankingTransactionSystem`
5. Click **Finish**.
6. Right-click the project > **Run As** > **Run on Server** (select Apache Tomcat 8.5 / 9 / 10).
7. The project will open in the browser at:  
   `http://localhost:8080/MultiBankingTransactionSystem/`

---

### Method 3: In IntelliJ IDEA (Ultimate or Community)
1. Open IntelliJ IDEA > **Open** > Select `MultiBankingTransactionSystem`.
2. IntelliJ will detect `pom.xml` or the Eclipse project files automatically.
3. Configure your Tomcat Run/Debug Configuration or run `runner.LocalServer` directly with 1-click.

---

## 🛡️ ACID Compliance & Atomic Transactions

In [`TransactionDAO.java`](src/com/banking/dao/TransactionDAO.java), money transfers are wrapped in an ACID transaction:

```java
connection.setAutoCommit(false);
// 1. Check sender balance (FOR UPDATE row lock)
// 2. Deduct from sender account
// 3. Credit to beneficiary account
// 4. Log transfer transaction
connection.commit(); // Atomic commit
```

If an error occurs at any stage, `connection.rollback()` automatically reverts all operations, guaranteeing zero balance discrepancy.

---

## 👩‍💻 Author & Credits

- **Sakshi** — BCA Graduate | MCA (AI & ML) Student  
- Passionate about Full-Stack Java Development, Database Architecture & Machine Learning.
