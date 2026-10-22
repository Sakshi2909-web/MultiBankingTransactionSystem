package com.banking.servlet;

import com.banking.dao.AccountDAO;
import com.banking.dao.TransactionDAO;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/transactions")
public class TransactionHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TransactionDAO transactionDAO = new TransactionDAO();
    private AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please login to view transactions.");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String filterAccount = request.getParameter("accountNumber");

        List<Account> userAccounts = accountDAO.getAccountsByUser(currentUser.getUserId());
        List<Transaction> transactions;

        if (filterAccount != null && !filterAccount.trim().isEmpty() && !"ALL".equalsIgnoreCase(filterAccount)) {
            transactions = transactionDAO.getTransactionsByAccount(filterAccount.trim());
        } else {
            transactions = transactionDAO.getTransactionsByUser(currentUser.getUserId());
        }

        request.setAttribute("userAccounts", userAccounts);
        request.setAttribute("transactions", transactions);
        request.setAttribute("selectedAccount", filterAccount != null ? filterAccount : "ALL");
        request.getRequestDispatcher("/transactions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
