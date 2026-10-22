<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.banking.model.User" %>
<%@ page import="com.banking.model.Account" %>
<%@ page import="com.banking.dao.AccountDAO" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please login first.");
        return;
    }

    AccountDAO accountDAO = new AccountDAO();
    List<Account> accounts = accountDAO.getAccountsByUser(currentUser.getUserId());
    String preselectedAcc = request.getParameter("acc");

    request.setAttribute("activePage", "withdraw");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Withdraw Money - Multi Banking System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="bg-light">

    <!-- Header Navbar -->
    <jsp:include page="/includes/navbar.jsp" />

    <main class="py-5">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-7 col-lg-6">

                    <!-- Alerts -->
                    <jsp:include page="/includes/alerts.jsp" />

                    <div class="bank-card p-4 p-sm-5 bg-white">
                        <div class="text-center mb-4">
                            <div class="stat-icon icon-amber mx-auto mb-3">
                                <i class="bi bi-arrow-up-circle fs-3 text-danger"></i>
                            </div>
                            <h3 class="fw-bold mb-1">Withdraw Money</h3>
                            <p class="text-muted small">Debit funds safely from your linked bank account</p>
                        </div>

                        <% if (accounts.isEmpty()) { %>
                            <div class="alert alert-warning text-center">
                                <p class="mb-2">You don't have any open bank accounts to withdraw from.</p>
                                <a href="<%= request.getContextPath() %>/create-account.jsp" class="btn btn-sm btn-primary">Open Account Now</a>
                            </div>
                        <% } else { %>
                            <form action="<%= request.getContextPath() %>/withdraw" method="POST" id="withdrawForm">
                                <div class="mb-3">
                                    <label for="accountNumberSelect" class="form-label">Select Bank Account</label>
                                    <select class="form-select" id="accountNumberSelect" name="accountNumber" required>
                                        <option value="" disabled <%= preselectedAcc == null ? "selected" : "" %>>-- Choose Account --</option>
                                        <% for (Account acc : accounts) { 
                                            boolean isSelected = preselectedAcc != null && preselectedAcc.equalsIgnoreCase(acc.getAccountNumber());
                                        %>
                                            <option value="<%= acc.getAccountNumber() %>" 
                                                    data-balance="<%= acc.getBalance() %>"
                                                    <%= isSelected ? "selected" : "" %>>
                                                <%= acc.getBankName() %> (<%= acc.getAccountNumber() %>) - Bal: ₹<%= String.format("%,.2f", acc.getBalance()) %>
                                            </option>
                                        <% } %>
                                    </select>
                                </div>

                                <div class="mb-3 <%= preselectedAcc == null ? "d-none" : "" %>">
                                    <div class="p-2 bg-light border rounded text-danger fw-semibold small" id="selectedAccountBalanceWrapper">
                                        <i class="bi bi-info-circle me-1"></i>
                                        <span id="selectedAccountBalance">
                                            <% if (preselectedAcc != null) {
                                                Account sel = accountDAO.getAccountByNumber(preselectedAcc);
                                                if (sel != null) { %>
                                                    Available Balance: ₹<%= String.format("%,.2f", sel.getBalance()) %>
                                                <% }
                                            } %>
                                        </span>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="withdrawAmount" class="form-label">Withdrawal Amount (₹)</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light fw-bold">₹</span>
                                        <input type="number" step="0.01" min="1" class="form-control" id="withdrawAmount" 
                                               name="amount" placeholder="e.g. 2000" required>
                                    </div>
                                </div>

                                <div class="mb-4">
                                    <label for="withdrawDesc" class="form-label">Description / Remarks (Optional)</label>
                                    <input type="text" class="form-control" id="withdrawDesc" name="description" 
                                           placeholder="e.g. ATM Withdrawal, Bill Payment">
                                </div>

                                <div class="d-flex gap-2">
                                    <a href="<%= request.getContextPath() %>/dashboard.jsp" class="btn btn-outline-secondary w-50 py-2">
                                        Cancel
                                    </a>
                                    <button type="submit" class="btn btn-danger w-50 py-2">
                                        <i class="bi bi-arrow-up-circle me-1"></i> Confirm Withdrawal
                                    </button>
                                </div>
                            </form>
                        <% } %>
                    </div>

                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <jsp:include page="/includes/footer.jsp" />

    <script>
        document.getElementById('withdrawForm')?.addEventListener('submit', function(e) {
            const select = document.getElementById('accountNumberSelect');
            const opt = select.options[select.selectedIndex];
            const bal = parseFloat(opt.getAttribute('data-balance') || "0");
            const amt = parseFloat(document.getElementById('withdrawAmount').value || "0");
            if (amt > bal) {
                e.preventDefault();
                alert("Insufficient balance! You can only withdraw up to ₹" + bal.toFixed(2));
            }
        });
    </script>
</body>
</html>
