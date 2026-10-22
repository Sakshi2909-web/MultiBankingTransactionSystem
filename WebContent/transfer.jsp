<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.banking.model.User" %>
<%@ page import="com.banking.model.Account" %>
<%@ page import="com.banking.model.Bank" %>
<%@ page import="com.banking.dao.AccountDAO" %>
<%@ page import="com.banking.dao.BankDAO" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please login first.");
        return;
    }

    AccountDAO accountDAO = new AccountDAO();
    BankDAO bankDAO = new BankDAO();

    List<Account> accounts = accountDAO.getAccountsByUser(currentUser.getUserId());
    List<Bank> allBanks = bankDAO.getAllBanks();
    String preselectedAcc = request.getParameter("from");

    request.setAttribute("activePage", "transfer");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fund Transfer - Multi Banking System</title>
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
                <div class="col-md-8 col-lg-7">

                    <!-- Alerts -->
                    <jsp:include page="/includes/alerts.jsp" />

                    <div class="bank-card p-4 p-sm-5 bg-white">
                        <div class="text-center mb-4">
                            <div class="stat-icon icon-purple mx-auto mb-3">
                                <i class="bi bi-arrow-left-right fs-3 text-primary"></i>
                            </div>
                            <h3 class="fw-bold mb-1">Inter-Bank Fund Transfer</h3>
                            <p class="text-muted small">Transfer funds between your accounts or to other customers instantly</p>
                        </div>

                        <% if (accounts.isEmpty()) { %>
                            <div class="alert alert-warning text-center">
                                <p class="mb-2">You don't have any open bank accounts to send money from.</p>
                                <a href="<%= request.getContextPath() %>/create-account.jsp" class="btn btn-sm btn-primary">Open Account Now</a>
                            </div>
                        <% } else { %>
                            <form action="<%= request.getContextPath() %>/transfer" method="POST" id="transferForm">
                                
                                <!-- 1. Source Account -->
                                <div class="mb-3">
                                    <label for="accountNumberSelect" class="form-label">Transfer From (Your Account)</label>
                                    <select class="form-select" id="accountNumberSelect" name="fromAccount" required>
                                        <option value="" disabled <%= preselectedAcc == null ? "selected" : "" %>>-- Select Sender Account --</option>
                                        <% for (Account acc : accounts) { 
                                            boolean isSelected = preselectedAcc != null && preselectedAcc.equalsIgnoreCase(acc.getAccountNumber());
                                        %>
                                            <option value="<%= acc.getAccountNumber() %>" 
                                                    data-balance="<%= acc.getBalance() %>"
                                                    <%= isSelected ? "selected" : "" %>>
                                                <%= acc.getBankName() %> (<%= acc.getAccountNumber() %>) - Balance: ₹<%= String.format("%,.2f", acc.getBalance()) %>
                                            </option>
                                        <% } %>
                                    </select>
                                </div>

                                <div class="mb-3 <%= preselectedAcc == null ? "d-none" : "" %>">
                                    <div class="p-2 bg-light border rounded text-primary fw-semibold small" id="selectedAccountBalanceWrapper">
                                        <i class="bi bi-wallet2 me-1"></i>
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

                                <!-- 2. Beneficiary Mode Selection -->
                                <div class="mb-3">
                                    <label class="form-label d-block">Transfer Method</label>
                                    <div class="btn-group w-100" role="group">
                                        <input type="radio" class="btn-check" name="transferMode" id="modeAccount" value="byAccount" checked>
                                        <label class="btn btn-outline-primary" for="modeAccount">
                                            <i class="bi bi-hash me-1"></i> Beneficiary Account Number
                                        </label>

                                        <input type="radio" class="btn-check" name="transferMode" id="modeEmail" value="byEmail">
                                        <label class="btn btn-outline-primary" for="modeEmail">
                                            <i class="bi bi-envelope me-1"></i> Receiver Email &amp; Bank
                                        </label>
                                    </div>
                                </div>

                                <!-- 3A. Target by Account Number -->
                                <div id="groupAccountMode" class="mb-3">
                                    <label for="toAccount" class="form-label">Beneficiary Account Number</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light"><i class="bi bi-credit-card"></i></span>
                                        <input type="text" class="form-control font-monospace" id="toAccount" name="toAccount" 
                                               placeholder="e.g. ICIC2001, SBIN2002, HDFC1002">
                                    </div>
                                    <div class="form-text small">Enter destination account number (supports any bank).</div>
                                </div>

                                <!-- 3B. Target by Email & Bank -->
                                <div id="groupEmailMode" class="mb-3 d-none">
                                    <div class="row g-2">
                                        <div class="col-md-7">
                                            <label for="receiverEmail" class="form-label">Receiver's Registered Email</label>
                                            <div class="input-group">
                                                <span class="input-group-text bg-light"><i class="bi bi-envelope"></i></span>
                                                <input type="email" class="form-control" id="receiverEmail" name="receiverEmail" 
                                                       placeholder="e.g. priya@gmail.com">
                                            </div>
                                        </div>
                                        <div class="col-md-5">
                                            <label for="receiverBankId" class="form-label">Receiver's Bank</label>
                                            <select class="form-select" id="receiverBankId" name="receiverBankId">
                                                <option value="" disabled selected>-- Bank --</option>
                                                <% for (Bank b : allBanks) { %>
                                                    <option value="<%= b.getBankId() %>"><%= b.getBankName() %></option>
                                                <% } %>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <!-- 4. Amount -->
                                <div class="mb-3">
                                    <label for="transferAmount" class="form-label">Transfer Amount (₹)</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light fw-bold">₹</span>
                                        <input type="number" step="0.01" min="1" class="form-control" id="transferAmount" 
                                               name="amount" placeholder="e.g. 1500" required>
                                    </div>
                                </div>

                                <!-- 5. Remarks -->
                                <div class="mb-4">
                                    <label for="transferDesc" class="form-label">Transaction Purpose / Remarks (Optional)</label>
                                    <input type="text" class="form-control" id="transferDesc" name="description" 
                                           placeholder="e.g. Project fee payment, Rent, Split bill">
                                </div>

                                <div class="d-flex gap-2">
                                    <a href="<%= request.getContextPath() %>/dashboard.jsp" class="btn btn-outline-secondary w-50 py-2">
                                        Cancel
                                    </a>
                                    <button type="submit" class="btn btn-bank-primary w-50 py-2">
                                        <i class="bi bi-send-check me-1"></i> Send Money Now
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
        document.getElementById('transferForm')?.addEventListener('submit', function(e) {
            const select = document.getElementById('accountNumberSelect');
            const opt = select.options[select.selectedIndex];
            const bal = parseFloat(opt.getAttribute('data-balance') || "0");
            const amt = parseFloat(document.getElementById('transferAmount').value || "0");
            if (amt > bal) {
                e.preventDefault();
                alert("Insufficient balance! Your current balance is ₹" + bal.toFixed(2));
                return;
            }

            const isByAccount = document.getElementById('modeAccount').checked;
            if (isByAccount) {
                const toAcc = document.getElementById('toAccount').value.trim();
                const fromAcc = select.value.trim();
                if (!toAcc) {
                    e.preventDefault();
                    alert("Please enter the beneficiary account number!");
                    return;
                }
                if (toAcc.toLowerCase() === fromAcc.toLowerCase()) {
                    e.preventDefault();
                    alert("Sender and beneficiary account cannot be the same!");
                    return;
                }
            } else {
                const recEmail = document.getElementById('receiverEmail').value.trim();
                const recBank = document.getElementById('receiverBankId').value;
                if (!recEmail || !recBank) {
                    e.preventDefault();
                    alert("Please specify both receiver email and receiver bank!");
                    return;
                }
            }
        });
    </script>
</body>
</html>
