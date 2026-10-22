<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String successMsg = request.getParameter("success");
    if (successMsg == null) {
        successMsg = (String) request.getAttribute("successMessage");
    }

    String errorMsg = request.getParameter("error");
    if (errorMsg == null) {
        errorMsg = (String) request.getAttribute("errorMessage");
    }
%>

<% if (successMsg != null && !successMsg.trim().isEmpty()) { %>
    <div class="alert alert-success alert-dismissible fade show d-flex align-items-center shadow-sm mb-4" role="alert">
        <i class="bi bi-check-circle-fill fs-5 me-2"></i>
        <div><strong>Success:</strong> <%= successMsg %></div>
        <button type="button" class="btn-close ms-auto" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
<% } %>

<% if (errorMsg != null && !errorMsg.trim().isEmpty()) { %>
    <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center shadow-sm mb-4" role="alert">
        <i class="bi bi-exclamation-triangle-fill fs-5 me-2"></i>
        <div><strong>Error:</strong> <%= errorMsg %></div>
        <button type="button" class="btn-close ms-auto" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
<% } %>
