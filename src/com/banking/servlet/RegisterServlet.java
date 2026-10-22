package com.banking.servlet;

import com.banking.dao.UserDAO;
import com.banking.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");

        if (name == null || email == null || password == null || 
            name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "All required fields must be filled!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        email = email.trim().toLowerCase();

        if (userDAO.emailExists(email)) {
            request.setAttribute("errorMessage", "An account with this email already exists!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        User newUser = new User(0, name.trim(), email, password.trim());
        newUser.setPhone(phone != null ? phone.trim() : "");

        boolean success = userDAO.register(newUser);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?success=Registration successful! Please login.");
        } else {
            request.setAttribute("errorMessage", "Registration failed due to a database error. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
