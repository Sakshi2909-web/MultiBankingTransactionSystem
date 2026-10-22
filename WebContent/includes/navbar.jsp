<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.banking.model.User" %>
<%
    User navUser = (User) session.getAttribute("currentUser");
    String activePage = (String) request.getAttribute("activePage");
    if (activePage == null) activePage = "";
%>

<nav class="navbar navbar-expand-lg navbar-dark navbar-bank sticky-top">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center gap-2" href="<%= request.getContextPath() %>/<%= navUser != null ? "dashboard.jsp" : "index.jsp" %>">
            <i class="bi bi-bank fs-4"></i>
            <span>Multi<span class="text-primary-emphasis" style="color: #60a5fa !important;">Bank</span> Pay</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#bankNavbar">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="bankNavbar">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <% if (navUser == null) { %>
                    <li class="nav-item">
                        <a class="nav-link <%= activePage.equals("home") ? "active" : "" %>" href="<%= request.getContextPath() %>/index.jsp">Home</a>
                    </li>
                <% } else { %>
                    <li class="nav-item">
                        <a class="nav-link <%= activePage.equals("dashboard") ? "active" : "" %>" href="<%= request.getContextPath() %>/dashboard.jsp">
                            <i class="bi bi-speedometer2 me-1"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <%= activePage.equals("create-account") ? "active" : "" %>" href="<%= request.getContextPath() %>/create-account.jsp">
                            <i class="bi bi-plus-circle me-1"></i> Open Account
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <%= activePage.equals("deposit") ? "active" : "" %>" href="<%= request.getContextPath() %>/deposit.jsp">
                            <i class="bi bi-arrow-down-circle me-1"></i> Deposit
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <%= activePage.equals("withdraw") ? "active" : "" %>" href="<%= request.getContextPath() %>/withdraw.jsp">
                            <i class="bi bi-arrow-up-circle me-1"></i> Withdraw
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <%= activePage.equals("transfer") ? "active" : "" %>" href="<%= request.getContextPath() %>/transfer.jsp">
                            <i class="bi bi-arrow-left-right me-1"></i> Transfer
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <%= activePage.equals("transactions") ? "active" : "" %>" href="<%= request.getContextPath() %>/transactions">
                            <i class="bi bi-receipt me-1"></i> Passbook
                        </a>
                    </li>
                <% } %>
            </ul>

            <ul class="navbar-nav ms-auto align-items-lg-center">
                <% if (navUser == null) { %>
                    <li class="nav-item me-2">
                        <a class="btn btn-outline-light btn-sm px-3" href="<%= request.getContextPath() %>/login.jsp">Login</a>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-primary btn-sm px-3" href="<%= request.getContextPath() %>/register.jsp">Register</a>
                    </li>
                <% } else { %>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle d-flex align-items-center gap-2 text-white" href="#" id="userMenu" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <div class="bg-primary rounded-circle d-flex align-items-center justify-content-center text-white" style="width: 32px; height: 32px; font-weight: bold;">
                                <%= navUser.getName().substring(0, 1).toUpperCase() %>
                            </div>
                            <span><%= navUser.getName() %></span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end shadow border-0" aria-labelledby="userMenu">
                            <li><h6 class="dropdown-header text-truncate" style="max-width: 220px;"><%= navUser.getEmail() %></h6></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="<%= request.getContextPath() %>/dashboard.jsp"><i class="bi bi-grid me-2"></i> My Accounts</a></li>
                            <li><a class="dropdown-item" href="<%= request.getContextPath() %>/transactions"><i class="bi bi-clock-history me-2"></i> Transaction History</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="<%= request.getContextPath() %>/logout"><i class="bi bi-box-arrow-right me-2"></i> Logout</a></li>
                        </ul>
                    </li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>
