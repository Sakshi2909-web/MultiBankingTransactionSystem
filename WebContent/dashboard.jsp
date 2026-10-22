<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.banking.model.User" %>
<%@ page import="com.banking.model.Account" %>
<%@ page import="com.banking.model.Transaction" %>
<%@ page import="com.banking.dao.AccountDAO" %>
<%@ page import="com.banking.dao.TransactionDAO" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please login to view dashboard.");
        return;
    }

    AccountDAO accountDAO = new AccountDAO();
    TransactionDAO transactionDAO = new TransactionDAO();

    List<Account> accounts = accountDAO.getAccountsByUser(currentUser.getUserId());
    double totalBalance = accountDAO.getTotalBalance(currentUser.getUserId());
    List<Transaction> recentTransactions = transactionDAO.getTransactionsByUser(currentUser.getUserId());
    
    // Keep top 5
    if (recentTransactions != null && recentTransactions.size() > 5) {
        recentTransactions = recentTransactions.subList(0, 5);
    }

    request.setAttribute("activePage", "dashboard");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Dashboard - Multi Banking System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

    <!-- Header Navbar -->
    <jsp:include page="/includes/navbar.jsp" />

    <main class="py-4">
        <div class="container">
            
            <!-- Alert Notifications -->
            <jsp:include page="/includes/alerts.jsp" />

            <!-- Welcome Header -->
            <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <div>
                    <h2 class="fw-bold mb-1">Welcome back, <%= currentUser.getName() %>! 👋</h2>
                    <p class="text-muted small mb-0">Customer ID: #<%= currentUser.getUserId() %> | Registered Email: <%= currentUser.getEmail() %></p>
                </div>
                <div class="d-flex gap-2 mt-3 mt-md-0">
                    <a href="<%= request.getContextPath() %>/create-account.jsp" class="btn btn-bank-primary btn-sm">
                        <i class="bi bi-plus-circle me-1"></i> Open New Bank Account
                    </a>
                    <a href="<%= request.getContextPath() %>/transfer.jsp" class="btn btn-outline-primary btn-sm">
                        <i class="bi bi-arrow-left-right me-1"></i> Transfer Funds
                    </a>
                </div>
            </div>

            <!-- Metric Summary Cards -->
            <div class="row g-3 mb-4">
                <div class="col-md-4">
                    <div class="stat-box">
                        <div class="stat-icon icon-blue">
                            <i class="bi bi-cash-stack"></i>
                        </div>
                        <div>
                            <span class="text-muted small text-uppercase fw-semibold">Total Combined Balance</span>
                            <h3 class="fw-bold mb-0">₹<%= String.format("%,.2f", totalBalance) %></h3>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="stat-box">
                        <div class="stat-icon icon-green">
                            <i class="bi bi-wallet2"></i>
                        </div>
                        <div>
                            <span class="text-muted small text-uppercase fw-semibold">Linked Bank Accounts</span>
                            <h3 class="fw-bold mb-0"><%= accounts.size() %> Active</h3>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="stat-box">
                        <div class="stat-icon icon-purple">
                            <i class="bi bi-shield-check"></i>
                        </div>
                        <div>
                            <span class="text-muted small text-uppercase fw-semibold">System Status</span>
                            <h5 class="fw-bold text-success mb-0"><i class="bi bi-check-circle-fill me-1"></i> All Services Active</h5>
                        </div>
                    </div>
                </div>
            </div>

            <!-- My Bank Accounts Section -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="fw-bold mb-0"><i class="bi bi-credit-card-2-front me-2 text-primary"></i>My Bank Accounts</h4>
                <a href="<%= request.getContextPath() %>/create-account.jsp" class="text-primary text-decoration-none small fw-semibold">
                    <i class="bi bi-plus-lg me-1"></i>Link Another Bank
                </a>
            </div>

            <% if (accounts.isEmpty()) { %>
                <div class="bank-card p-5 text-center bg-white mb-4">
                    <div class="stat-icon icon-amber mx-auto mb-3">
                        <i class="bi bi-bank fs-2"></i>
                    </div>
                    <h5 class="fw-bold">No Active Bank Accounts Yet</h5>
                    <p class="text-muted small mb-3">You have not opened or linked any bank account yet. Choose from SBI, HDFC, ICICI, Axis or PNB.</p>
                    <a href="<%= request.getContextPath() %>/create-account.jsp" class="btn btn-bank-primary">
                        <i class="bi bi-plus-circle me-1"></i> Open Your First Account
                    </a>
                </div>
            <% } else { %>
                <div class="row g-4 mb-4">
                    <% for (Account acc : accounts) { 
                        String cardTheme = "card-default";
                        String code = acc.getBankCode() != null ? acc.getBankCode().toUpperCase() : "";
                        if (code.contains("SBI")) cardTheme = "card-sbi";
                        else if (code.contains("HDFC")) cardTheme = "card-hdfc";
                        else if (code.contains("ICICI")) cardTheme = "card-icici";
                        else if (code.contains("AXIS")) cardTheme = "card-axis";
                        else if (code.contains("PNB")) cardTheme = "card-pnb";
                    %>
                        <div class="col-md-6 col-lg-4">
                            <div class="bank-atm-card <%= cardTheme %>">
                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="fw-bold text-uppercase fs-6 tracking-wide"><%= acc.getBankName() %></span>
                                    <span class="badge bg-white text-dark py-1 px-2 font-monospace"><%= acc.getAccountType() %></span>
                                </div>
                                <div class="my-3">
                                    <div class="card-chip mb-2"></div>
                                    <div class="card-acc-num"><%= acc.getAccountNumber() %></div>
                                </div>
                                <div>
                                    <div class="d-flex justify-content-between align-items-end mb-2">
                                        <div>
                                            <small class="text-white-50 text-uppercase d-block" style="font-size: 0.65rem;">Account Holder</small>
                                            <span class="fw-semibold small"><%= currentUser.getName().toUpperCase() %></span>
                                        </div>
                                        <div class="text-end">
                                            <small class="text-white-50 text-uppercase d-block" style="font-size: 0.65rem;">Available Balance</small>
                                            <span class="card-balance">₹<%= String.format("%,.2f", acc.getBalance()) %></span>
                                        </div>
                                    </div>
                                    <div class="d-flex gap-2 pt-2 border-top border-white border-opacity-25">
                                        <a href="<%= request.getContextPath() %>/deposit.jsp?acc=<%= acc.getAccountNumber() %>" class="btn btn-sm btn-light flex-fill py-1 fw-semibold text-primary">
                                            <i class="bi bi-arrow-down-circle me-1"></i> Deposit
                                        </a>
                                        <a href="<%= request.getContextPath() %>/withdraw.jsp?acc=<%= acc.getAccountNumber() %>" class="btn btn-sm btn-outline-light flex-fill py-1 fw-semibold">
                                            <i class="bi bi-arrow-up-circle me-1"></i> Withdraw
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    <% } %>
                </div>
            <% } %>

            <!-- Quick Action Buttons -->
            <div class="bank-card p-3 mb-4 bg-white">
                <div class="row row-cols-2 row-cols-md-4 g-2 text-center">
                    <div class="col">
                        <a href="<%= request.getContextPath() %>/deposit.jsp" class="btn btn-light w-100 py-3 border text-primary fw-semibold">
                            <i class="bi bi-arrow-down-circle fs-4 d-block mb-1"></i> Deposit Cash
                        </a>
                    </div>
                    <div class="col">
                        <a href="<%= request.getContextPath() %>/withdraw.jsp" class="btn btn-light w-100 py-3 border text-danger fw-semibold">
                            <i class="bi bi-arrow-up-circle fs-4 d-block mb-1"></i> Withdraw Cash
                        </a>
                    </div>
                    <div class="col">
                        <a href="<%= request.getContextPath() %>/transfer.jsp" class="btn btn-light w-100 py-3 border text-success fw-semibold">
                            <i class="bi bi-arrow-left-right fs-4 d-block mb-1"></i> Fund Transfer
                        </a>
                    </div>
                    <div class="col">
                        <a href="<%= request.getContextPath() %>/transactions" class="btn btn-light w-100 py-3 border text-secondary fw-semibold">
                            <i class="bi bi-receipt fs-4 d-block mb-1"></i> Full Passbook
                        </a>
                    </div>
                </div>
            </div>

            <!-- Recent Transactions Table -->
            <div class="bank-card bg-white p-4">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold mb-0"><i class="bi bi-clock-history me-2 text-primary"></i>Recent Transactions</h5>
                    <a href="<%= request.getContextPath() %>/transactions" class="text-primary text-decoration-none small fw-semibold">
                        View All Activity <i class="bi bi-arrow-right"></i>
                    </a>
                </div>

                <% if (recentTransactions == null || recentTransactions.isEmpty()) { %>
                    <p class="text-muted small text-center py-4 mb-0">No transaction records found for your accounts yet.</p>
                <% } else { %>
                    <div class="table-responsive">
                        <table class="table table-bank align-middle mb-0">
                            <thead>
                                <tr>
                                    <th>Ref / ID</th>
                                    <th>Type</th>
                                    <th>From Account</th>
                                    <th>To Account</th>
                                    <th>Amount</th>
                                    <th>Date &amp; Time</th>
                                    <th>Description</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Transaction txn : recentTransactions) { 
                                    String badgeClass = "badge-txn-transfer";
                                    String type = txn.getType() != null ? txn.getType().toUpperCase() : "";
                                    if ("DEPOSIT".equals(type)) badgeClass = "badge-txn-credit";
                                    else if ("WITHDRAW".equals(type)) badgeClass = "badge-txn-debit";
                                %>
                                    <tr>
                                        <td class="font-monospace text-muted small"><%= txn.getTransactionRef() != null ? txn.getTransactionRef() : ("#TXN-" + txn.getTransactionId()) %></td>
                                        <td><span class="<%= badgeClass %>"><%= txn.getType() %></span></td>
                                        <td class="fw-semibold"><%= txn.getFromAccount() %></td>
                                        <td class="fw-semibold"><%= txn.getToAccount() %></td>
                                        <td class="fw-bold <%= "DEPOSIT".equals(type) ? "text-success" : ("WITHDRAW".equals(type) ? "text-danger" : "text-primary") %>">
                                            <%= "DEPOSIT".equals(type) ? "+" : ("WITHDRAW".equals(type) ? "-" : "") %>₹<%= String.format("%,.2f", txn.getAmount()) %>
                                        </td>
                                        <td class="text-muted small"><%= txn.getTimestamp() %></td>
                                        <td class="text-muted small text-truncate" style="max-width: 200px;"><%= txn.getDescription() != null ? txn.getDescription() : "-" %></td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } %>
            </div>

        </div>
    </main>

    <!-- Footer -->
    <jsp:include page="/includes/footer.jsp" />

</body>
</html>
