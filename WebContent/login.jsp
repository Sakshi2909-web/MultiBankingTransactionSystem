<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Login - Multi Banking System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="d-flex flex-column min-vh-100 bg-light">

    <!-- Header Navbar -->
    <jsp:include page="/includes/navbar.jsp" />

    <main class="flex-grow-1 d-flex align-items-center py-5">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6 col-lg-5">
                    
                    <!-- Alert Messages -->
                    <jsp:include page="/includes/alerts.jsp" />

                    <div class="bank-card p-4 p-sm-5 bg-white">
                        <div class="text-center mb-4">
                            <div class="stat-icon icon-blue mx-auto mb-3">
                                <i class="bi bi-shield-lock fs-3"></i>
                            </div>
                            <h3 class="fw-bold mb-1">Customer Login</h3>
                            <p class="text-muted small">Access your multi-bank dashboard and services</p>
                        </div>

                        <!-- One-Click Demo Accounts -->
                        <div class="p-3 mb-4 rounded-3 bg-light border">
                            <div class="d-flex align-items-center justify-content-between mb-2">
                                <small class="text-muted fw-bold text-uppercase" style="font-size: 0.75rem;">
                                    <i class="bi bi-lightning-charge-fill text-warning me-1"></i> Quick Demo Login:
                                </small>
                            </div>
                            <div class="d-flex gap-2">
                                <button type="button" class="btn btn-sm btn-outline-primary flex-fill" onclick="fillDemoUser('rahul@gmail.com', '123456')">
                                    <i class="bi bi-person-fill"></i> Rahul (SBI/HDFC)
                                </button>
                                <button type="button" class="btn btn-sm btn-outline-success flex-fill" onclick="fillDemoUser('priya@gmail.com', '123456')">
                                    <i class="bi bi-person-fill"></i> Priya (ICICI/SBI)
                                </button>
                            </div>
                        </div>

                        <form action="<%= request.getContextPath() %>/login" method="POST">
                            <div class="mb-3">
                                <label for="loginEmail" class="form-label">Email Address</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light text-muted"><i class="bi bi-envelope"></i></span>
                                    <input type="email" class="form-control" id="loginEmail" name="email" 
                                           placeholder="e.g. rahul@gmail.com" required 
                                           value="<%= request.getAttribute("prevEmail") != null ? request.getAttribute("prevEmail") : "" %>">
                                </div>
                            </div>

                            <div class="mb-4">
                                <label for="loginPassword" class="form-label">Password</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light text-muted"><i class="bi bi-key"></i></span>
                                    <input type="password" class="form-control" id="loginPassword" name="password" 
                                           placeholder="Enter your password" required>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-bank-primary w-100 py-2 mb-3">
                                <i class="bi bi-box-arrow-in-right me-2"></i> Secure Login
                            </button>

                            <div class="text-center text-muted small">
                                Don't have a banking account? 
                                <a href="<%= request.getContextPath() %>/register.jsp" class="fw-semibold text-primary text-decoration-none">Register here</a>
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
