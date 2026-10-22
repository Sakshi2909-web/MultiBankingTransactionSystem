<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.banking.model.User" %>
<%@ page import="com.banking.model.Bank" %>
<%@ page import="com.banking.model.Account" %>
<%@ page import="com.banking.dao.BankDAO" %>
<%@ page import="com.banking.dao.AccountDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please login first.");
        return;
    }

    BankDAO bankDAO = new BankDAO();
    AccountDAO accountDAO = new AccountDAO();

    List<Bank> allBanks = bankDAO.getAllBanks();
    List<Account> userAccounts = accountDAO.getAccountsByUser(currentUser.getUserId());

    Set<Integer> existingBankIds = new HashSet<>();
    for (Account acc : userAccounts) {
        existingBankIds.add(acc.getBankId());
    }

    request.setAttribute("activePage", "create-account");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Open Bank Account - Multi Banking System</title>
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
                                <i class="bi bi-bank fs-3"></i>
                            </div>
                            <h3 class="fw-bold mb-1">Open New Bank Account</h3>
                            <p class="text-muted small">Choose a partner bank and create an instant savings account</p>
                        </div>

                        <form action="<%= request.getContextPath() %>/create-account" method="POST">
                            <div class="mb-3">
                                <label for="bankSelect" class="form-label">Select Partner Bank</label>
                                <select class="form-select" id="bankSelect" name="bankId" required>
                                    <option value="" disabled selected>-- Choose Bank --</option>
                                    <% for (Bank b : allBanks) { 
                                        boolean alreadyHas = existingBankIds.contains(b.getBankId());
                                    %>
                                        <option value="<%= b.getBankId() %>" <%= alreadyHas ? "disabled" : "" %>>
                                            <%= b.getBankName() %> (<%= b.getBankCode() %>) <%= alreadyHas ? "[Already Linked]" : "" %>
                                        </option>
                                    <% } %>
                                </select>
                                <div class="form-text small">You can create one account per partner bank.</div>
                            </div>

                            <div class="mb-3">
                                <label for="initialDeposit" class="form-label">Initial Opening Deposit (₹)</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light fw-bold">₹</span>
                                    <input type="number" step="0.01" min="0" class="form-control" id="initialDeposit" 
                                           name="initialDeposit" placeholder="0.00" value="1000.00">
                                </div>
                                <div class="form-text small">Zero balance opening is allowed, or start with any deposit.</div>
                            </div>

                            <div class="mb-4">
                                <label class="form-label">Account Type</label>
                                <input type="text" class="form-control bg-light" value="SAVINGS ACCOUNT" readonly>
                            </div>

                            <div class="form-check mb-4">
                                <input class="form-check-input" type="checkbox" id="terms" required checked>
                                <label class="form-check-label small text-muted" for="terms">
                                    I agree to the bank account opening terms &amp; multi-banking compliance policy.
                                </label>
                            </div>

                            <div class="d-flex gap-2">
                                <a href="<%= request.getContextPath() %>/dashboard.jsp" class="btn btn-outline-secondary w-50 py-2">
                                    Cancel
                                </a>
                                <button type="submit" class="btn btn-bank-primary w-50 py-2">
                                    <i class="bi bi-check-circle me-1"></i> Open Account
                                </button>
                            </div>
                        </form>
                    </div>

                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <jsp:include page="/includes/footer.jsp" />

</body>
</html>
