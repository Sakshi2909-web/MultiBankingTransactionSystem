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
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please login to view transactions.");
        return;
    }

    AccountDAO accountDAO = new AccountDAO();
    TransactionDAO transactionDAO = new TransactionDAO();

    @SuppressWarnings("unchecked")
    List<Account> userAccounts = (List<Account>) request.getAttribute("userAccounts");
    if (userAccounts == null) {
        userAccounts = accountDAO.getAccountsByUser(currentUser.getUserId());
    }

    String selectedAccount = (String) request.getAttribute("selectedAccount");
    if (selectedAccount == null) {
        selectedAccount = request.getParameter("accountNumber");
        if (selectedAccount == null) selectedAccount = "ALL";
    }

    @SuppressWarnings("unchecked")
    List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
    if (transactions == null) {
        if (!"ALL".equalsIgnoreCase(selectedAccount)) {
            transactions = transactionDAO.getTransactionsByAccount(selectedAccount);
        } else {
            transactions = transactionDAO.getTransactionsByUser(currentUser.getUserId());
        }
    }

    int depositCount = 0;
    int withdrawCount = 0;
    int transferCount = 0;
    if (transactions != null) {
        for (Transaction t : transactions) {
            String type = t.getType() != null ? t.getType().toUpperCase() : "";
            if ("DEPOSIT".equals(type)) depositCount++;
            else if ("WITHDRAW".equals(type)) withdrawCount++;
            else if ("TRANSFER".equals(type)) transferCount++;
        }
    }

    request.setAttribute("activePage", "transactions");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Passbook &amp; Transaction History - Multi Banking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="bg-light">

    <!-- Header Navbar -->
    <jsp:include page="/includes/navbar.jsp" />

    <main class="py-4">
        <div class="container">

            <!-- Alerts -->
            <jsp:include page="/includes/alerts.jsp" />

            <!-- Page Header -->
            <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <div>
                    <h2 class="fw-bold mb-1"><i class="bi bi-journal-text me-2 text-primary"></i>Passbook &amp; Transaction History</h2>
                    <p class="text-muted small mb-0">Complete audit trail of all deposits, withdrawals, and inter-bank transfers</p>
                </div>
                <div class="d-flex gap-2 mt-3 mt-md-0">
                    <button type="button" class="btn btn-outline-secondary btn-sm" onclick="printPassbook()">
                        <i class="bi bi-printer me-1"></i> Print Statement
                    </button>
                    <a href="<%= request.getContextPath() %>/dashboard.jsp" class="btn btn-outline-primary btn-sm">
                        <i class="bi bi-arrow-left me-1"></i> Back to Dashboard
                    </a>
                </div>
            </div>

            <!-- Summary Chips -->
            <div class="row g-3 mb-4">
                <div class="col-6 col-md-3">
                    <div class="stat-box py-3">
                        <div class="stat-icon icon-blue" style="width:42px; height:42px; font-size:1.1rem;">
                            <i class="bi bi-receipt"></i>
                        </div>
                        <div>
                            <span class="text-muted small">Total</span>
                            <h5 class="fw-bold mb-0"><%= transactions != null ? transactions.size() : 0 %></h5>
                        </div>
                    </div>
                </div>
                <div class="col-6 col-md-3">
                    <div class="stat-box py-3">
                        <div class="stat-icon icon-green" style="width:42px; height:42px; font-size:1.1rem;">
                            <i class="bi bi-arrow-down-circle"></i>
                        </div>
                        <div>
                            <span class="text-muted small">Deposits</span>
                            <h5 class="fw-bold mb-0 text-success"><%= depositCount %></h5>
                        </div>
                    </div>
                </div>
                <div class="col-6 col-md-3">
                    <div class="stat-box py-3">
                        <div class="stat-icon icon-amber" style="width:42px; height:42px; font-size:1.1rem;">
                            <i class="bi bi-arrow-up-circle"></i>
                        </div>
                        <div>
                            <span class="text-muted small">Withdrawals</span>
                            <h5 class="fw-bold mb-0 text-danger"><%= withdrawCount %></h5>
                        </div>
                    </div>
                </div>
                <div class="col-6 col-md-3">
                    <div class="stat-box py-3">
                        <div class="stat-icon icon-purple" style="width:42px; height:42px; font-size:1.1rem;">
                            <i class="bi bi-arrow-left-right"></i>
                        </div>
                        <div>
                            <span class="text-muted small">Transfers</span>
                            <h5 class="fw-bold mb-0 text-primary"><%= transferCount %></h5>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Filters & Search Toolbar -->
            <div class="bank-card p-3 mb-4 bg-white">
                <form action="<%= request.getContextPath() %>/transactions" method="GET" class="row g-3 align-items-center">
                    <div class="col-md-5">
                        <label for="filterAccountSelect" class="form-label small mb-1">Filter by Account</label>
                        <select class="form-select form-select-sm" id="filterAccountSelect" name="accountNumber" onchange="this.form.submit()">
                            <option value="ALL" <%= "ALL".equalsIgnoreCase(selectedAccount) ? "selected" : "" %>>All Linked Accounts</option>
                            <% for (Account acc : userAccounts) { %>
                                <option value="<%= acc.getAccountNumber() %>" <%= acc.getAccountNumber().equalsIgnoreCase(selectedAccount) ? "selected" : "" %>>
                                    <%= acc.getBankName() %> (<%= acc.getAccountNumber() %>)
                                </option>
                            <% } %>
                        </select>
                    </div>

                    <div class="col-md-7">
                        <label for="transactionSearch" class="form-label small mb-1">Live Search in Passbook</label>
                        <div class="input-group input-group-sm">
                            <span class="input-group-text bg-light"><i class="bi bi-search"></i></span>
                            <input type="text" class="form-control" id="transactionSearch" placeholder="Type ref, account number, type or description...">
                        </div>
                    </div>
                </form>
            </div>

            <!-- Passbook Statement Table -->
            <div class="bank-card bg-white p-4">
                <% if (transactions == null || transactions.isEmpty()) { %>
                    <div class="text-center py-5">
                        <i class="bi bi-folder2-open display-4 text-muted d-block mb-3"></i>
                        <h5 class="text-muted">No transactions found</h5>
                        <p class="small text-muted">Perform a deposit, withdrawal, or transfer to see entries here.</p>
                    </div>
                <% } else { %>
                    <div class="table-responsive">
                        <table class="table table-bank align-middle" id="transactionsTable">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Transaction Ref</th>
                                    <th>Type</th>
                                    <th>From Account</th>
                                    <th>To Account</th>
                                    <th>Amount</th>
                                    <th>Date &amp; Time</th>
                                    <th>Remarks</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% 
                                int rowNum = 1;
                                for (Transaction t : transactions) { 
                                    String badgeClass = "badge-txn-transfer";
                                    String type = t.getType() != null ? t.getType().toUpperCase() : "";
                                    if ("DEPOSIT".equals(type)) badgeClass = "badge-txn-credit";
                                    else if ("WITHDRAW".equals(type)) badgeClass = "badge-txn-debit";
                                %>
                                    <tr>
                                        <td class="text-muted small"><%= rowNum++ %></td>
                                        <td class="font-monospace fw-semibold small text-primary"><%= t.getTransactionRef() != null ? t.getTransactionRef() : ("#TXN-" + t.getTransactionId()) %></td>
                                        <td><span class="<%= badgeClass %>"><%= t.getType() %></span></td>
                                        <td class="font-monospace fw-semibold"><%= t.getFromAccount() %></td>
                                        <td class="font-monospace fw-semibold"><%= t.getToAccount() %></td>
                                        <td class="fw-bold <%= "DEPOSIT".equals(type) ? "text-success" : ("WITHDRAW".equals(type) ? "text-danger" : "text-primary") %>">
                                            <%= "DEPOSIT".equals(type) ? "+" : ("WITHDRAW".equals(type) ? "-" : "") %>₹<%= String.format("%,.2f", t.getAmount()) %>
                                        </td>
                                        <td class="text-muted small text-nowrap"><%= t.getTimestamp() %></td>
                                        <td class="small text-muted"><%= t.getDescription() != null ? t.getDescription() : "-" %></td>
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
