package com.banking.servlet;

import com.banking.dao.AccountDAO;
import com.banking.dao.BankDAO;
import com.banking.model.Account;
import com.banking.model.Bank;
import com.banking.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/create-account")
public class CreateAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AccountDAO accountDAO = new AccountDAO();
    private BankDAO bankDAO = new BankDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please login first.");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/create-account.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Session expired. Please login again.");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String bankIdStr = request.getParameter("bankId");
        String initialDepositStr = request.getParameter("initialDeposit");

        if (bankIdStr == null || bankIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/create-account.jsp?error=Please select a bank!");
            return;
        }

        int bankId;
        double initialDeposit = 0.0;
        try {
            bankId = Integer.parseInt(bankIdStr);
            if (initialDepositStr != null && !initialDepositStr.trim().isEmpty()) {
                initialDeposit = Double.parseDouble(initialDepositStr.trim());
                if (initialDeposit < 0) {
                    response.sendRedirect(request.getContextPath() + "/create-account.jsp?error=Initial deposit cannot be negative!");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/create-account.jsp?error=Invalid bank or deposit amount format!");
            return;
        }

        Bank bank = bankDAO.getBankById(bankId);
        if (bank == null) {
            response.sendRedirect(request.getContextPath() + "/create-account.jsp?error=Selected bank is invalid!");
            return;
        }

        if (accountDAO.hasAccountInBank(currentUser.getUserId(), bankId)) {
            response.sendRedirect(request.getContextPath() + "/create-account.jsp?error=You already have an account in " + bank.getBankName() + "!");
            return;
        }

        Account newAccount = accountDAO.createAccount(currentUser.getUserId(), bankId, initialDeposit);
        if (newAccount != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp?success=Account successfully created in " + 
                bank.getBankName() + "! Account Number: " + newAccount.getAccountNumber());
        } else {
            response.sendRedirect(request.getContextPath() + "/create-account.jsp?error=Could not create bank account. Please try again.");
        }
    }
}
