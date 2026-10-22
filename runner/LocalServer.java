package runner;

import com.banking.dao.AccountDAO;
import com.banking.dao.BankDAO;
import com.banking.dao.TransactionDAO;
import com.banking.dao.UserDAO;
import com.banking.model.Account;
import com.banking.model.Bank;
import com.banking.model.Transaction;
import com.banking.model.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Standalone Local Development Runner for Multi Banking Transaction System
 * Enables testing the dynamic web application without needing external Apache Tomcat installation.
 */
public class LocalServer {

    private static int PORT = 8080;
    private static final UserDAO userDAO = new UserDAO();
    private static final BankDAO bankDAO = new BankDAO();
    private static final AccountDAO accountDAO = new AccountDAO();
    private static final TransactionDAO transactionDAO = new TransactionDAO();

    // In-memory session tracking for local runner
    private static final Map<String, User> sessions = new HashMap<>();

    public static void main(String[] args) throws Exception {
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.trim().isEmpty()) {
            try {
                PORT = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {}
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        System.out.println("==========================================================");
        System.out.println("  🏦 Multi Banking Transaction System - Local Dev Server   ");
        System.out.println("==========================================================");
        System.out.println("  Server started at: http://localhost:" + PORT + "/");
        System.out.println("  Standard Eclipse Dynamic Web Project is also configured.");
        System.out.println("  Press Ctrl+C to stop the server.");
        System.out.println("==========================================================");

        // Serve Static Files (CSS, JS)
        server.createContext("/css/", new StaticFileHandler("WebContent/css", "text/css"));
        server.createContext("/js/", new StaticFileHandler("WebContent/js", "application/javascript"));

        // Web Endpoints
        server.createContext("/", new DynamicRouterHandler());

        server.setExecutor(null);
        server.start();

        // Try to automatically open browser on Windows
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", "http://localhost:" + PORT + "/").start();
            }
        } catch (Exception ignored) {}
    }

    static class StaticFileHandler implements HttpHandler {
        private final String baseDir;
        private final String mimeType;

        public StaticFileHandler(String baseDir, String mimeType) {
            this.baseDir = baseDir;
            this.mimeType = mimeType;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String pathStr = exchange.getRequestURI().getPath();
            String filename = pathStr.substring(pathStr.lastIndexOf('/') + 1);
            Path filePath = Paths.get(baseDir, filename);

            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                byte[] bytes = Files.readAllBytes(filePath);
                exchange.getResponseHeaders().set("Content-Type", mimeType);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }

    static class DynamicRouterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
            User currentUser = getCurrentUser(exchange);

            if (path.equals("/") || path.equals("/index.jsp") || path.equals("/index.html")) {
                sendHtml(exchange, renderHome(currentUser));
            } else if (path.equals("/login") || path.equals("/login.jsp")) {
                if ("POST".equalsIgnoreCase(method)) {
                    Map<String, String> form = parseFormData(exchange);
                    String email = form.get("email");
                    String password = form.get("password");
                    User user = userDAO.login(email != null ? email.trim() : "", password != null ? password.trim() : "");
                    if (user != null) {
                        String sessionId = UUID.randomUUID().toString();
                        sessions.put(sessionId, user);
                        exchange.getResponseHeaders().add("Set-Cookie", "BANK_SESSION=" + sessionId + "; Path=/");
                        redirect(exchange, "/dashboard");
                    } else {
                        redirect(exchange, "/login?error=Invalid email or password!");
                    }
                } else {
                    sendHtml(exchange, renderLogin(queryParams.get("error"), queryParams.get("success")));
                }
            } else if (path.equals("/register") || path.equals("/register.jsp")) {
                if ("POST".equalsIgnoreCase(method)) {
                    Map<String, String> form = parseFormData(exchange);
                    String name = form.get("name");
                    String email = form.get("email");
                    String password = form.get("password");
                    String phone = form.get("phone");

                    if (userDAO.emailExists(email)) {
                        redirect(exchange, "/register?error=Email already exists!");
                    } else {
                        User u = new User(0, name, email, password);
                        u.setPhone(phone);
                        if (userDAO.register(u)) {
                            redirect(exchange, "/login?success=Registration successful! Please login.");
                        } else {
                            redirect(exchange, "/register?error=Registration failed due to database error.");
                        }
                    }
                } else {
                    sendHtml(exchange, renderRegister(queryParams.get("error")));
                }
            } else if (path.equals("/logout")) {
                for (Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
                    if ("cookie".equalsIgnoreCase(header.getKey()) && header.getValue() != null) {
                        for (String cookieHeader : header.getValue()) {
                            for (String part : cookieHeader.split(";")) {
                                String[] kv = part.trim().split("=", 2);
                                if (kv.length == 2 && "BANK_SESSION".equalsIgnoreCase(kv[0].trim())) {
                                    sessions.remove(kv[1].trim());
                                }
                            }
                        }
                    }
                }
                exchange.getResponseHeaders().add("Set-Cookie", "BANK_SESSION=; Path=/; Max-Age=0");
                redirect(exchange, "/login?success=You have logged out successfully.");
            } else if (path.equals("/dashboard") || path.equals("/dashboard.jsp")) {
                if (currentUser == null) {
                    redirect(exchange, "/login?error=Please login to view dashboard.");
                    return;
                }
                sendHtml(exchange, renderDashboard(currentUser, queryParams.get("success"), queryParams.get("error")));
            } else if (path.equals("/create-account") || path.equals("/create-account.jsp")) {
                if (currentUser == null) {
                    redirect(exchange, "/login?error=Please login first.");
                    return;
                }
                if ("POST".equalsIgnoreCase(method)) {
                    Map<String, String> form = parseFormData(exchange);
                    int bankId = Integer.parseInt(form.getOrDefault("bankId", "1"));
                    double deposit = Double.parseDouble(form.getOrDefault("initialDeposit", "0"));
                    Account acc = accountDAO.createAccount(currentUser.getUserId(), bankId, deposit);
                    if (acc != null) {
                        redirect(exchange, "/dashboard?success=Account created in " + acc.getBankName() + "! Number: " + acc.getAccountNumber());
                    } else {
                        redirect(exchange, "/create-account?error=Account creation failed or account already exists.");
                    }
                } else {
                    sendHtml(exchange, renderCreateAccount(currentUser, queryParams.get("error")));
                }
            } else if (path.equals("/deposit") || path.equals("/deposit.jsp")) {
                if (currentUser == null) {
                    redirect(exchange, "/login?error=Please login first.");
                    return;
                }
                if ("POST".equalsIgnoreCase(method)) {
                    Map<String, String> form = parseFormData(exchange);
                    String acc = form.get("accountNumber");
                    double amt = Double.parseDouble(form.getOrDefault("amount", "0"));
                    String desc = form.get("description");
                    if (transactionDAO.deposit(acc, amt, desc)) {
                        redirect(exchange, "/dashboard?success=Successfully deposited ₹" + amt + " into " + acc + "!");
                    } else {
                        redirect(exchange, "/deposit?error=Deposit failed.");
                    }
                } else {
                    sendHtml(exchange, renderDeposit(currentUser, queryParams.get("acc"), queryParams.get("error")));
                }
            } else if (path.equals("/withdraw") || path.equals("/withdraw.jsp")) {
                if (currentUser == null) {
                    redirect(exchange, "/login?error=Please login first.");
                    return;
                }
                if ("POST".equalsIgnoreCase(method)) {
                    Map<String, String> form = parseFormData(exchange);
                    String acc = form.get("accountNumber");
                    double amt = Double.parseDouble(form.getOrDefault("amount", "0"));
                    String desc = form.get("description");
                    if (transactionDAO.withdraw(acc, amt, desc)) {
                        redirect(exchange, "/dashboard?success=Successfully withdrew ₹" + amt + " from " + acc + "!");
                    } else {
                        redirect(exchange, "/withdraw?error=Insufficient balance or withdrawal failed.");
                    }
                } else {
                    sendHtml(exchange, renderWithdraw(currentUser, queryParams.get("acc"), queryParams.get("error")));
                }
            } else if (path.equals("/transfer") || path.equals("/transfer.jsp")) {
                if (currentUser == null) {
                    redirect(exchange, "/login?error=Please login first.");
                    return;
                }
                if ("POST".equalsIgnoreCase(method)) {
                    Map<String, String> form = parseFormData(exchange);
                    String fromAcc = form.get("fromAccount");
                    String toAcc = form.get("toAccount");
                    String transferMode = form.get("transferMode");
                    double amt = Double.parseDouble(form.getOrDefault("amount", "0"));
                    String desc = form.get("description");

                    if ("byEmail".equalsIgnoreCase(transferMode)) {
                        String recEmail = form.get("receiverEmail");
                        int recBankId = Integer.parseInt(form.getOrDefault("receiverBankId", "1"));
                        User recUser = userDAO.getUserByEmail(recEmail);
                        if (recUser != null) {
                            Account recAcc = accountDAO.getAccountByUserAndBank(recUser.getUserId(), recBankId);
                            if (recAcc != null) toAcc = recAcc.getAccountNumber();
                        }
                    }

                    if (toAcc == null || toAcc.trim().isEmpty()) {
                        redirect(exchange, "/transfer?error=Receiver account not found!");
                        return;
                    }

                    if (transactionDAO.transferFunds(fromAcc, toAcc, amt, desc)) {
                        redirect(exchange, "/dashboard?success=Successfully transferred ₹" + amt + " to " + toAcc + "!");
                    } else {
                        redirect(exchange, "/transfer?error=Transfer failed! Check sender balance or receiver account.");
                    }
                } else {
                    sendHtml(exchange, renderTransfer(currentUser, queryParams.get("from"), queryParams.get("error")));
                }
            } else if (path.equals("/transactions") || path.equals("/transactions.jsp")) {
                if (currentUser == null) {
                    redirect(exchange, "/login?error=Please login first.");
                    return;
                }
                sendHtml(exchange, renderTransactions(currentUser, queryParams.get("accountNumber")));
            } else {
                redirect(exchange, "/");
            }
        }

        private User getCurrentUser(HttpExchange exchange) {
            for (Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
                if ("cookie".equalsIgnoreCase(header.getKey()) && header.getValue() != null) {
                    for (String cookieHeader : header.getValue()) {
                        for (String part : cookieHeader.split(";")) {
                            String[] kv = part.trim().split("=", 2);
                            if (kv.length == 2 && "BANK_SESSION".equalsIgnoreCase(kv[0].trim())) {
                                return sessions.get(kv[1].trim());
                            }
                        }
                    }
                }
            }
            return null;
        }

        private void redirect(HttpExchange exchange, String location) throws IOException {
            exchange.getResponseHeaders().set("Location", location);
            exchange.sendResponseHeaders(302, -1);
        }

        private void sendHtml(HttpExchange exchange, String html) throws IOException {
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private Map<String, String> parseQueryParams(String query) {
            Map<String, String> map = new HashMap<>();
            if (query == null) return map;
            for (String param : query.split("&")) {
                String[] pair = param.split("=", 2);
                if (pair.length == 2) {
                    map.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8), URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                }
            }
            return map;
        }

        private Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
            InputStream in = exchange.getRequestBody();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return parseQueryParams(out.toString(StandardCharsets.UTF_8));
        }

        // HTML Renderers reusing Bootstrap UI
        private String getHeader(User user, String title) {
            return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>" +
                   "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                   "<title>" + title + " - Multi Banking System</title>" +
                   "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>" +
                   "<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css'>" +
                   "<link href='https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap' rel='stylesheet'>" +
                   "<link rel='stylesheet' href='/css/style.css'></head><body>" +
                   "<nav class='navbar navbar-expand-lg navbar-dark navbar-bank sticky-top'><div class='container'>" +
                   "<a class='navbar-brand d-flex align-items-center gap-2' href='/'><i class='bi bi-bank fs-4'></i><span>Multi<span style='color:#60a5fa !important;'>Bank</span> Pay</span></a>" +
                   "<div class='collapse navbar-collapse d-flex justify-content-between'>" +
                   "<ul class='navbar-nav me-auto'>" +
                   (user == null ? "<li class='nav-item'><a class='nav-link' href='/'>Home</a></li>" :
                    "<li class='nav-item'><a class='nav-link' href='/dashboard'><i class='bi bi-speedometer2 me-1'></i> Dashboard</a></li>" +
                    "<li class='nav-item'><a class='nav-link' href='/create-account'><i class='bi bi-plus-circle me-1'></i> Open Account</a></li>" +
                    "<li class='nav-item'><a class='nav-link' href='/deposit'><i class='bi bi-arrow-down-circle me-1'></i> Deposit</a></li>" +
                    "<li class='nav-item'><a class='nav-link' href='/withdraw'><i class='bi bi-arrow-up-circle me-1'></i> Withdraw</a></li>" +
                    "<li class='nav-item'><a class='nav-link' href='/transfer'><i class='bi bi-arrow-left-right me-1'></i> Transfer</a></li>" +
                    "<li class='nav-item'><a class='nav-link' href='/transactions'><i class='bi bi-receipt me-1'></i> Passbook</a></li>") +
                   "</ul>" +
                   "<ul class='navbar-nav align-items-center'>" +
                   (user == null ? "<li class='nav-item me-2'><a class='btn btn-outline-light btn-sm px-3' href='/login'>Login</a></li>" +
                                   "<li class='nav-item'><a class='btn btn-primary btn-sm px-3' href='/register'>Register</a></li>" :
                    "<li class='nav-item text-white me-3 small fw-bold'><i class='bi bi-person-circle me-1'></i> " + user.getName() + "</li>" +
                    "<li class='nav-item'><a class='btn btn-outline-danger btn-sm' href='/logout'><i class='bi bi-box-arrow-right'></i> Logout</a></li>") +
                   "</ul></div></div></nav><main>";
        }

        private String getFooter() {
            return "</main><footer><div class='container text-center text-secondary small'>" +
                   "Multi Banking Transaction System &copy; 2026 | Built with Java Servlets, JSP, JDBC & MySQL" +
                   "</div></footer><script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js'></script>" +
                   "<script src='/js/script.js'></script></body></html>";
        }

        private String renderAlerts(String success, String error) {
            StringBuilder sb = new StringBuilder();
            if (success != null && !success.isEmpty()) {
                sb.append("<div class='alert alert-success alert-dismissible fade show d-flex align-items-center mb-4'>")
                  .append("<i class='bi bi-check-circle-fill me-2 fs-5'></i><div><strong>Success:</strong> ").append(success).append("</div>")
                  .append("<button type='button' class='btn-close ms-auto' data-bs-dismiss='alert'></button></div>");
            }
            if (error != null && !error.isEmpty()) {
                sb.append("<div class='alert alert-danger alert-dismissible fade show d-flex align-items-center mb-4'>")
                  .append("<i class='bi bi-exclamation-triangle-fill me-2 fs-5'></i><div><strong>Error:</strong> ").append(error).append("</div>")
                  .append("<button type='button' class='btn-close ms-auto' data-bs-dismiss='alert'></button></div>");
            }
            return sb.toString();
        }

        private String renderHome(User user) {
            return getHeader(user, "Home") +
                   "<section class='py-5' style='background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%); color: white;'>" +
                   "<div class='container py-4'><div class='row align-items-center g-5'><div class='col-lg-7'>" +
                   "<span class='badge bg-primary px-3 py-2 rounded-pill mb-3 text-uppercase fw-bold'><i class='bi bi-shield-lock-fill me-1'></i> Next-Gen Multi-Banking</span>" +
                   "<h1 class='display-4 fw-bold lh-sm mb-3'>One Unified Platform For All Your <span style='color: #60a5fa;'>Bank Accounts</span></h1>" +
                   "<p class='lead text-light opacity-75 mb-4'>Seamlessly manage accounts across SBI, HDFC, ICICI, Axis and PNB with real-time transfers, deposits and digital passbook.</p>" +
                   "<div class='d-flex gap-3'>" +
                   (user == null ? "<a href='/register' class='btn btn-primary btn-lg px-4 shadow'><i class='bi bi-person-plus-fill me-2'></i> Open Account</a>" +
                                   "<a href='/login' class='btn btn-outline-light btn-lg px-4'><i class='bi bi-box-arrow-in-right me-2'></i> Customer Login</a>" :
                                   "<a href='/dashboard' class='btn btn-primary btn-lg px-4 shadow'><i class='bi bi-speedometer2 me-2'></i> Go to Dashboard</a>") +
                   "</div></div>" +
                   "<div class='col-lg-5 text-center'>" +
                   "<div class='bank-atm-card card-sbi text-start mx-auto' style='max-width: 380px;'>" +
                   "<div class='d-flex justify-content-between align-items-center'><span class='fw-bold fs-5'>STATE BANK OF INDIA</span><i class='bi bi-wifi fs-4'></i></div>" +
                   "<div class='card-chip my-3'></div><div class='card-acc-num'>SBIN •••• 1001</div>" +
                   "<div class='d-flex justify-content-between align-items-end mt-2'><div><small class='text-white-50 text-uppercase d-block' style='font-size: 0.7rem;'>Card Holder</small><span class='fw-semibold'>RAHUL SHARMA</span></div>" +
                   "<div class='text-end'><small class='text-white-50 text-uppercase d-block' style='font-size: 0.7rem;'>Balance</small><span class='fs-5 fw-bold'>₹25,000.00</span></div></div></div>" +
                   "</div></div></div></section>" + getFooter();
        }

        private String renderLogin(String error, String success) {
            return getHeader(null, "Login") +
                   "<div class='container py-5'><div class='row justify-content-center'><div class='col-md-6 col-lg-5'>" +
                   renderAlerts(success, error) +
                   "<div class='bank-card p-4 p-sm-5 bg-white'>" +
                   "<div class='text-center mb-4'><div class='stat-icon icon-blue mx-auto mb-3'><i class='bi bi-shield-lock fs-3'></i></div><h3 class='fw-bold mb-1'>Customer Login</h3><p class='text-muted small'>Access your multi-bank dashboard</p></div>" +
                   "<div class='p-3 mb-4 rounded-3 bg-light border'><small class='text-muted fw-bold text-uppercase d-block mb-2'><i class='bi bi-lightning-charge-fill text-warning me-1'></i> Quick Demo Login:</small>" +
                   "<div class='d-flex gap-2'>" +
                   "<button type='button' class='btn btn-sm btn-outline-primary flex-fill' onclick=\"fillDemoUser('rahul@gmail.com','123456')\"><i class='bi bi-person-fill'></i> Rahul</button>" +
                   "<button type='button' class='btn btn-sm btn-outline-success flex-fill' onclick=\"fillDemoUser('priya@gmail.com','123456')\"><i class='bi bi-person-fill'></i> Priya</button>" +
                   "</div></div>" +
                   "<form action='/login' method='POST'>" +
                   "<div class='mb-3'><label class='form-label'>Email Address</label><input type='email' class='form-control' id='loginEmail' name='email' required placeholder='e.g. rahul@gmail.com'></div>" +
                   "<div class='mb-4'><label class='form-label'>Password</label><input type='password' class='form-control' id='loginPassword' name='password' required placeholder='Enter password'></div>" +
                   "<button type='submit' class='btn btn-bank-primary w-100 py-2 mb-3'><i class='bi bi-box-arrow-in-right me-2'></i> Secure Login</button>" +
                   "<div class='text-center small text-muted'>Don't have an account? <a href='/register' class='text-primary fw-semibold'>Register here</a></div>" +
                   "</form></div></div></div></div>" + getFooter();
        }

        private String renderRegister(String error) {
            return getHeader(null, "Register") +
                   "<div class='container py-5'><div class='row justify-content-center'><div class='col-md-7 col-lg-6'>" +
                   renderAlerts(null, error) +
                   "<div class='bank-card p-4 p-sm-5 bg-white'>" +
                   "<div class='text-center mb-4'><div class='stat-icon icon-green mx-auto mb-3'><i class='bi bi-person-plus fs-3'></i></div><h3 class='fw-bold mb-1'>Create Account</h3><p class='text-muted small'>Register for multi-bank services</p></div>" +
                   "<form action='/register' method='POST'>" +
                   "<div class='mb-3'><label class='form-label'>Full Name</label><input type='text' class='form-control' name='name' required placeholder='e.g. Amit Kumar'></div>" +
                   "<div class='row'><div class='col-md-6 mb-3'><label class='form-label'>Email Address</label><input type='email' class='form-control' name='email' required placeholder='name@example.com'></div>" +
                   "<div class='col-md-6 mb-3'><label class='form-label'>Mobile Number</label><input type='tel' class='form-control' name='phone' placeholder='10-digit mobile'></div></div>" +
                   "<div class='row'><div class='col-md-6 mb-4'><label class='form-label'>Password</label><input type='password' class='form-control' name='password' required minlength='6' placeholder='Min 6 chars'></div>" +
                   "<div class='col-md-6 mb-4'><label class='form-label'>Confirm Password</label><input type='password' class='form-control' name='confirmPassword' required minlength='6' placeholder='Re-enter'></div></div>" +
                   "<button type='submit' class='btn btn-bank-primary w-100 py-2 mb-3'><i class='bi bi-check2-circle me-2'></i> Register New Customer</button>" +
                   "<div class='text-center small text-muted'>Already registered? <a href='/login' class='text-primary fw-semibold'>Login here</a></div>" +
                   "</form></div></div></div></div>" + getFooter();
        }

        private String renderDashboard(User user, String success, String error) {
            List<Account> accounts = accountDAO.getAccountsByUser(user.getUserId());
            double totalBal = accountDAO.getTotalBalance(user.getUserId());
            List<Transaction> txns = transactionDAO.getTransactionsByUser(user.getUserId());
            if (txns != null && txns.size() > 5) txns = txns.subList(0, 5);

            StringBuilder sb = new StringBuilder();
            sb.append(getHeader(user, "Dashboard"));
            sb.append("<div class='container py-4'>");
            sb.append(renderAlerts(success, error));
            sb.append("<div class='d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom'>")
              .append("<div><h2 class='fw-bold mb-1'>Welcome back, ").append(user.getName()).append("! 👋</h2>")
              .append("<p class='text-muted small mb-0'>Customer ID: #").append(user.getUserId()).append(" | Email: ").append(user.getEmail()).append("</p></div>")
              .append("<div class='d-flex gap-2 mt-3 mt-md-0'><a href='/create-account' class='btn btn-bank-primary btn-sm'><i class='bi bi-plus-circle me-1'></i> Open New Bank Account</a>")
              .append("<a href='/transfer' class='btn btn-outline-primary btn-sm'><i class='bi bi-arrow-left-right me-1'></i> Transfer Funds</a></div></div>");

            // Metrics
            sb.append("<div class='row g-3 mb-4'>")
              .append("<div class='col-md-4'><div class='stat-box'><div class='stat-icon icon-blue'><i class='bi bi-cash-stack'></i></div><div><span class='text-muted small text-uppercase fw-semibold'>Total Balance</span><h3 class='fw-bold mb-0'>₹").append(String.format("%,.2f", totalBal)).append("</h3></div></div></div>")
              .append("<div class='col-md-4'><div class='stat-box'><div class='stat-icon icon-green'><i class='bi bi-wallet2'></i></div><div><span class='text-muted small text-uppercase fw-semibold'>Active Accounts</span><h3 class='fw-bold mb-0'>").append(accounts.size()).append(" Linked</h3></div></div></div>")
              .append("<div class='col-md-4'><div class='stat-box'><div class='stat-icon icon-purple'><i class='bi bi-shield-check'></i></div><div><span class='text-muted small text-uppercase fw-semibold'>System Status</span><h5 class='fw-bold text-success mb-0'><i class='bi bi-check-circle-fill me-1'></i> All Services Online</h5></div></div></div></div>");

            // Accounts grid
            sb.append("<div class='d-flex justify-content-between align-items-center mb-3'><h4 class='fw-bold mb-0'><i class='bi bi-credit-card-2-front me-2 text-primary'></i>My Bank Accounts</h4>")
              .append("<a href='/create-account' class='text-primary text-decoration-none small fw-semibold'><i class='bi bi-plus-lg me-1'></i>Link Another Bank</a></div>");

            if (accounts.isEmpty()) {
                sb.append("<div class='bank-card p-5 text-center bg-white mb-4'><h5 class='fw-bold'>No Active Bank Accounts</h5><p class='text-muted small mb-3'>You have not linked any accounts yet.</p><a href='/create-account' class='btn btn-bank-primary'>Open Account Now</a></div>");
            } else {
                sb.append("<div class='row g-4 mb-4'>");
                for (Account a : accounts) {
                    String theme = "card-default";
                    String code = a.getBankCode() != null ? a.getBankCode().toUpperCase() : "";
                    if (code.contains("SBI")) theme = "card-sbi";
                    else if (code.contains("HDFC")) theme = "card-hdfc";
                    else if (code.contains("ICICI")) theme = "card-icici";
                    else if (code.contains("AXIS")) theme = "card-axis";
                    else if (code.contains("PNB")) theme = "card-pnb";

                    sb.append("<div class='col-md-6 col-lg-4'><div class='bank-atm-card ").append(theme).append("'>")
                      .append("<div class='d-flex justify-content-between align-items-center'><span class='fw-bold text-uppercase fs-6'>").append(a.getBankName()).append("</span><span class='badge bg-white text-dark py-1 px-2 font-monospace'>SAVINGS</span></div>")
                      .append("<div class='my-3'><div class='card-chip mb-2'></div><div class='card-acc-num'>").append(a.getAccountNumber()).append("</div></div>")
                      .append("<div><div class='d-flex justify-content-between align-items-end mb-2'><div><small class='text-white-50 text-uppercase d-block' style='font-size:0.65rem;'>Holder</small><span class='fw-semibold small'>").append(user.getName().toUpperCase()).append("</span></div>")
                      .append("<div class='text-end'><small class='text-white-50 text-uppercase d-block' style='font-size:0.65rem;'>Balance</small><span class='card-balance'>₹").append(String.format("%,.2f", a.getBalance())).append("</span></div></div>")
                      .append("<div class='d-flex gap-2 pt-2 border-top border-white border-opacity-25'><a href='/deposit?acc=").append(a.getAccountNumber()).append("' class='btn btn-sm btn-light flex-fill py-1 fw-semibold text-primary'><i class='bi bi-arrow-down-circle me-1'></i> Deposit</a>")
                      .append("<a href='/withdraw?acc=").append(a.getAccountNumber()).append("' class='btn btn-sm btn-outline-light flex-fill py-1 fw-semibold'><i class='bi bi-arrow-up-circle me-1'></i> Withdraw</a></div></div></div></div>");
                }
                sb.append("</div>");
            }

            // Quick actions
            sb.append("<div class='bank-card p-3 mb-4 bg-white'><div class='row row-cols-2 row-cols-md-4 g-2 text-center'>")
              .append("<div class='col'><a href='/deposit' class='btn btn-light w-100 py-3 border text-primary fw-semibold'><i class='bi bi-arrow-down-circle fs-4 d-block mb-1'></i> Deposit Cash</a></div>")
              .append("<div class='col'><a href='/withdraw' class='btn btn-light w-100 py-3 border text-danger fw-semibold'><i class='bi bi-arrow-up-circle fs-4 d-block mb-1'></i> Withdraw Cash</a></div>")
              .append("<div class='col'><a href='/transfer' class='btn btn-light w-100 py-3 border text-success fw-semibold'><i class='bi bi-arrow-left-right fs-4 d-block mb-1'></i> Fund Transfer</a></div>")
              .append("<div class='col'><a href='/transactions' class='btn btn-light w-100 py-3 border text-secondary fw-semibold'><i class='bi bi-receipt fs-4 d-block mb-1'></i> Full Passbook</a></div></div></div>");

            // Recent activity table
            sb.append("<div class='bank-card bg-white p-4'><h5 class='fw-bold mb-3'><i class='bi bi-clock-history me-2 text-primary'></i>Recent Transactions</h5>");
            if (txns == null || txns.isEmpty()) {
                sb.append("<p class='text-muted small text-center py-4 mb-0'>No transactions yet.</p>");
            } else {
                sb.append("<div class='table-responsive'><table class='table table-bank align-middle mb-0'><thead><tr><th>Ref</th><th>Type</th><th>From</th><th>To</th><th>Amount</th><th>Date</th></tr></thead><tbody>");
                for (Transaction t : txns) {
                    String bClass = "badge-txn-transfer";
                    if ("DEPOSIT".equalsIgnoreCase(t.getType())) bClass = "badge-txn-credit";
                    else if ("WITHDRAW".equalsIgnoreCase(t.getType())) bClass = "badge-txn-debit";
                    sb.append("<tr><td class='font-monospace small text-muted'>").append(t.getTransactionRef() != null ? t.getTransactionRef() : ("#TXN-" + t.getTransactionId()))
                      .append("</td><td><span class='").append(bClass).append("'>").append(t.getType()).append("</span></td>")
                      .append("<td class='fw-semibold'>").append(t.getFromAccount()).append("</td>")
                      .append("<td class='fw-semibold'>").append(t.getToAccount()).append("</td>")
                      .append("<td class='fw-bold ").append("DEPOSIT".equalsIgnoreCase(t.getType()) ? "text-success" : ("WITHDRAW".equalsIgnoreCase(t.getType()) ? "text-danger" : "text-primary")).append("'>")
                      .append("DEPOSIT".equalsIgnoreCase(t.getType()) ? "+" : ("WITHDRAW".equalsIgnoreCase(t.getType()) ? "-" : ""))
                      .append("₹").append(String.format("%,.2f", t.getAmount())).append("</td>")
                      .append("<td class='text-muted small'>").append(t.getTimestamp()).append("</td></tr>");
                }
                sb.append("</tbody></table></div>");
            }
            sb.append("</div></div>");
            sb.append(getFooter());
            return sb.toString();
        }

        private String renderCreateAccount(User user, String error) {
            List<Bank> allBanks = bankDAO.getAllBanks();
            List<Account> accounts = accountDAO.getAccountsByUser(user.getUserId());
            Set<Integer> existing = new HashSet<>();
            for (Account a : accounts) existing.add(a.getBankId());

            StringBuilder sb = new StringBuilder();
            sb.append(getHeader(user, "Open Account"));
            sb.append("<div class='container py-5'><div class='row justify-content-center'><div class='col-md-7 col-lg-6'>")
              .append(renderAlerts(null, error))
              .append("<div class='bank-card p-4 p-sm-5 bg-white'><div class='text-center mb-4'><div class='stat-icon icon-blue mx-auto mb-3'><i class='bi bi-bank fs-3'></i></div><h3 class='fw-bold mb-1'>Open New Bank Account</h3><p class='text-muted small'>Choose a partner bank</p></div>")
              .append("<form action='/create-account' method='POST'><div class='mb-3'><label class='form-label'>Select Partner Bank</label><select class='form-select' name='bankId' required>")
              .append("<option value='' disabled selected>-- Choose Bank --</option>");
            for (Bank b : allBanks) {
                boolean has = existing.contains(b.getBankId());
                sb.append("<option value='").append(b.getBankId()).append("' ").append(has ? "disabled" : "").append(">")
                  .append(b.getBankName()).append(" (").append(b.getBankCode()).append(") ").append(has ? "[Already Linked]" : "").append("</option>");
            }
            sb.append("</select></div><div class='mb-3'><label class='form-label'>Initial Deposit (₹)</label><input type='number' step='0.01' min='0' class='form-control' name='initialDeposit' value='1000.00'></div>")
              .append("<button type='submit' class='btn btn-bank-primary w-100 py-2'>Open Account</button></form></div></div></div></div>");
            sb.append(getFooter());
            return sb.toString();
        }

        private String renderDeposit(User user, String preselectedAcc, String error) {
            List<Account> accounts = accountDAO.getAccountsByUser(user.getUserId());
            StringBuilder sb = new StringBuilder();
            sb.append(getHeader(user, "Deposit"));
            sb.append("<div class='container py-5'><div class='row justify-content-center'><div class='col-md-7 col-lg-6'>")
              .append(renderAlerts(null, error))
              .append("<div class='bank-card p-4 p-sm-5 bg-white'><div class='text-center mb-4'><div class='stat-icon icon-blue mx-auto mb-3'><i class='bi bi-arrow-down-circle fs-3 text-primary'></i></div><h3 class='fw-bold mb-1'>Deposit Money</h3></div>")
              .append("<form action='/deposit' method='POST'><div class='mb-3'><label class='form-label'>Select Account</label><select class='form-select' name='accountNumber' required>");
            for (Account a : accounts) {
                boolean sel = preselectedAcc != null && preselectedAcc.equalsIgnoreCase(a.getAccountNumber());
                sb.append("<option value='").append(a.getAccountNumber()).append("' ").append(sel ? "selected" : "").append(">")
                  .append(a.getBankName()).append(" (").append(a.getAccountNumber()).append(") - Bal: ₹").append(String.format("%,.2f", a.getBalance())).append("</option>");
            }
            sb.append("</select></div><div class='mb-3'><label class='form-label'>Deposit Amount (₹)</label><input type='number' step='0.01' min='1' class='form-control' name='amount' required placeholder='e.g. 5000'></div>")
              .append("<div class='mb-4'><label class='form-label'>Remarks</label><input type='text' class='form-control' name='description' placeholder='Cash Deposit'></div>")
              .append("<button type='submit' class='btn btn-bank-primary w-100 py-2'>Confirm Deposit</button></form></div></div></div></div>");
            sb.append(getFooter());
            return sb.toString();
        }

        private String renderWithdraw(User user, String preselectedAcc, String error) {
            List<Account> accounts = accountDAO.getAccountsByUser(user.getUserId());
            StringBuilder sb = new StringBuilder();
            sb.append(getHeader(user, "Withdraw"));
            sb.append("<div class='container py-5'><div class='row justify-content-center'><div class='col-md-7 col-lg-6'>")
              .append(renderAlerts(null, error))
              .append("<div class='bank-card p-4 p-sm-5 bg-white'><div class='text-center mb-4'><div class='stat-icon icon-amber mx-auto mb-3'><i class='bi bi-arrow-up-circle fs-3 text-danger'></i></div><h3 class='fw-bold mb-1'>Withdraw Money</h3></div>")
              .append("<form action='/withdraw' method='POST'><div class='mb-3'><label class='form-label'>Select Account</label><select class='form-select' name='accountNumber' required>");
            for (Account a : accounts) {
                boolean sel = preselectedAcc != null && preselectedAcc.equalsIgnoreCase(a.getAccountNumber());
                sb.append("<option value='").append(a.getAccountNumber()).append("' ").append(sel ? "selected" : "").append(">")
                  .append(a.getBankName()).append(" (").append(a.getAccountNumber()).append(") - Bal: ₹").append(String.format("%,.2f", a.getBalance())).append("</option>");
            }
            sb.append("</select></div><div class='mb-3'><label class='form-label'>Withdrawal Amount (₹)</label><input type='number' step='0.01' min='1' class='form-control' name='amount' required placeholder='e.g. 2000'></div>")
              .append("<div class='mb-4'><label class='form-label'>Remarks</label><input type='text' class='form-control' name='description' placeholder='Cash Withdrawal'></div>")
              .append("<button type='submit' class='btn btn-danger w-100 py-2'>Confirm Withdrawal</button></form></div></div></div></div>");
            sb.append(getFooter());
            return sb.toString();
        }

        private String renderTransfer(User user, String preselectedAcc, String error) {
            List<Account> accounts = accountDAO.getAccountsByUser(user.getUserId());
            List<Bank> allBanks = bankDAO.getAllBanks();
            StringBuilder sb = new StringBuilder();
            sb.append(getHeader(user, "Transfer"));
            sb.append("<div class='container py-5'><div class='row justify-content-center'><div class='col-md-8 col-lg-7'>")
              .append(renderAlerts(null, error))
              .append("<div class='bank-card p-4 p-sm-5 bg-white'><div class='text-center mb-4'><div class='stat-icon icon-purple mx-auto mb-3'><i class='bi bi-arrow-left-right fs-3 text-primary'></i></div><h3 class='fw-bold mb-1'>Fund Transfer</h3></div>")
              .append("<form action='/transfer' method='POST'><div class='mb-3'><label class='form-label'>From Account</label><select class='form-select' name='fromAccount' required>");
            for (Account a : accounts) {
                boolean sel = preselectedAcc != null && preselectedAcc.equalsIgnoreCase(a.getAccountNumber());
                sb.append("<option value='").append(a.getAccountNumber()).append("' ").append(sel ? "selected" : "").append(">")
                  .append(a.getBankName()).append(" (").append(a.getAccountNumber()).append(") - Bal: ₹").append(String.format("%,.2f", a.getBalance())).append("</option>");
            }
            sb.append("</select></div><div class='mb-3'><label class='form-label'>Beneficiary Account Number</label><input type='text' class='form-control font-monospace' name='toAccount' placeholder='e.g. SBIN2002 or ICIC2001' required></div>")
              .append("<div class='mb-3'><label class='form-label'>Transfer Amount (₹)</label><input type='number' step='0.01' min='1' class='form-control' name='amount' required placeholder='e.g. 1500'></div>")
              .append("<div class='mb-4'><label class='form-label'>Remarks</label><input type='text' class='form-control' name='description' placeholder='Payment / Rent'></div>")
              .append("<button type='submit' class='btn btn-bank-primary w-100 py-2'>Send Money</button></form></div></div></div></div>");
            sb.append(getFooter());
            return sb.toString();
        }

        private String renderTransactions(User user, String filterAcc) {
            List<Account> accounts = accountDAO.getAccountsByUser(user.getUserId());
            List<Transaction> txns;
            if (filterAcc != null && !filterAcc.isEmpty() && !"ALL".equalsIgnoreCase(filterAcc)) {
                txns = transactionDAO.getTransactionsByAccount(filterAcc);
            } else {
                txns = transactionDAO.getTransactionsByUser(user.getUserId());
            }

            StringBuilder sb = new StringBuilder();
            sb.append(getHeader(user, "Passbook"));
            sb.append("<div class='container py-4'><div class='d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom'>")
              .append("<div><h2 class='fw-bold mb-1'><i class='bi bi-journal-text me-2 text-primary'></i>Digital Passbook</h2><p class='text-muted small mb-0'>Complete transaction history</p></div>")
              .append("<button type='button' class='btn btn-outline-secondary btn-sm' onclick='window.print()'><i class='bi bi-printer me-1'></i> Print Statement</button></div>")
              .append("<div class='bank-card bg-white p-4'>");

            if (txns == null || txns.isEmpty()) {
                sb.append("<p class='text-muted text-center py-4 mb-0'>No transactions found.</p>");
            } else {
                sb.append("<div class='table-responsive'><table class='table table-bank align-middle'><thead><tr><th>Ref</th><th>Type</th><th>From</th><th>To</th><th>Amount</th><th>Date</th><th>Remarks</th></tr></thead><tbody>");
                for (Transaction t : txns) {
                    String bClass = "badge-txn-transfer";
                    if ("DEPOSIT".equalsIgnoreCase(t.getType())) bClass = "badge-txn-credit";
                    else if ("WITHDRAW".equalsIgnoreCase(t.getType())) bClass = "badge-txn-debit";
                    sb.append("<tr><td class='font-monospace small text-primary'>").append(t.getTransactionRef() != null ? t.getTransactionRef() : ("#TXN-" + t.getTransactionId()))
                      .append("</td><td><span class='").append(bClass).append("'>").append(t.getType()).append("</span></td>")
                      .append("<td class='font-monospace'>").append(t.getFromAccount()).append("</td>")
                      .append("<td class='font-monospace'>").append(t.getToAccount()).append("</td>")
                      .append("<td class='fw-bold ").append("DEPOSIT".equalsIgnoreCase(t.getType()) ? "text-success" : ("WITHDRAW".equalsIgnoreCase(t.getType()) ? "text-danger" : "text-primary")).append("'>")
                      .append("DEPOSIT".equalsIgnoreCase(t.getType()) ? "+" : ("WITHDRAW".equalsIgnoreCase(t.getType()) ? "-" : ""))
                      .append("₹").append(String.format("%,.2f", t.getAmount())).append("</td>")
                      .append("<td class='text-muted small'>").append(t.getTimestamp()).append("</td>")
                      .append("<td class='small text-muted'>").append(t.getDescription() != null ? t.getDescription() : "-").append("</td></tr>");
                }
                sb.append("</tbody></table></div>");
            }
            sb.append("</div></div>");
            sb.append(getFooter());
            return sb.toString();
        }
    }
}
