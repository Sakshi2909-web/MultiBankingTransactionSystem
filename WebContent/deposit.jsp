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

    request.setAttribute("activePage", "deposit");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Deposit Money - Multi Banking System</title>
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
                            <div class="stat-icon icon-blue mx-auto mb-3">
                                <i class="bi bi-arrow-down-circle fs-3 text-primary"></i>
                            </div>
                            <h3 class="fw-bold mb-1">Deposit Money</h3>
                            <p class="text-muted small">Credit funds instantly into your chosen bank account</p>
                        </div>

                        <% if (accounts.isEmpty()) { %>
                            <div class="alert alert-warning text-center">
                                <p class="mb-2">You don't have any open bank accounts to deposit into.</p>
                                <a href="<%= request.getContextPath() %>/create-account.jsp" class="btn btn-sm btn-primary">Open Account Now</a>
                            </div>
                        <% } else { %>
                            <form action="<%= request.getContextPath() %>/deposit" method="POST">
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
                                    <div class="p-2 bg-light border rounded text-primary fw-semibold small" id="selectedAccountBalanceWrapper">
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
                                    <label for="depositAmount" class="form-label">Deposit Amount (₹)</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light fw-bold">₹</span>
                                        <input type="number" step="0.01" min="1" class="form-control" id="depositAmount" 
                                               name="amount" placeholder="e.g. 5000" required>
                                    </div>
                                </div>

                                <div class="mb-4">
                                    <label for="depositDesc" class="form-label">Description / Remarks (Optional)</label>
                                    <input type="text" class="form-control" id="depositDesc" name="description" 
                                           placeholder="e.g. Cash Deposit, Monthly Savings">
                                </div>

                                <div class="d-flex gap-2">
                                    <a href="<%= request.getContextPath() %>/dashboard.jsp" class="btn btn-outline-secondary w-50 py-2">
                                        Cancel
                                    </a>
                                    <button type="submit" class="btn btn-bank-primary w-50 py-2">
                                        <i class="bi bi-arrow-down-circle me-1"></i> Confirm Deposit
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

</body>
</html>
