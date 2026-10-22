<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Registration - Multi Banking System</title>
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
                <div class="col-md-7 col-lg-6">
                    
                    <!-- Alert Messages -->
                    <jsp:include page="/includes/alerts.jsp" />

                    <div class="bank-card p-4 p-sm-5 bg-white">
                        <div class="text-center mb-4">
                            <div class="stat-icon icon-green mx-auto mb-3">
                                <i class="bi bi-person-plus fs-3"></i>
                            </div>
                            <h3 class="fw-bold mb-1">Create Account</h3>
                            <p class="text-muted small">Register to start managing multiple bank accounts</p>
                        </div>

                        <form action="<%= request.getContextPath() %>/register" method="POST" id="registerForm">
                            <div class="mb-3">
                                <label for="regName" class="form-label">Full Name</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light text-muted"><i class="bi bi-person"></i></span>
                                    <input type="text" class="form-control" id="regName" name="name" 
                                           placeholder="e.g. Amit Kumar" required>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="regEmail" class="form-label">Email Address</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light text-muted"><i class="bi bi-envelope"></i></span>
                                        <input type="email" class="form-control" id="regEmail" name="email" 
                                               placeholder="name@example.com" required>
                                    </div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="regPhone" class="form-label">Mobile Number</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light text-muted"><i class="bi bi-phone"></i></span>
                                        <input type="tel" class="form-control" id="regPhone" name="phone" 
                                               placeholder="10-digit mobile" pattern="[0-9]{10}">
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-4">
                                    <label for="regPassword" class="form-label">Password</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light text-muted"><i class="bi bi-lock"></i></span>
                                        <input type="password" class="form-control" id="regPassword" name="password" 
                                               placeholder="Min 6 characters" minlength="6" required>
                                    </div>
                                </div>
                                <div class="col-md-6 mb-4">
                                    <label for="regConfirmPassword" class="form-label">Confirm Password</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light text-muted"><i class="bi bi-shield-check"></i></span>
                                        <input type="password" class="form-control" id="regConfirmPassword" 
                                               placeholder="Re-enter password" minlength="6" required>
                                    </div>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-bank-primary w-100 py-2 mb-3">
                                <i class="bi bi-check2-circle me-2"></i> Register New Customer
                            </button>

                            <div class="text-center text-muted small">
                                Already registered? 
                                <a href="<%= request.getContextPath() %>/login.jsp" class="fw-semibold text-primary text-decoration-none">Login here</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <jsp:include page="/includes/footer.jsp" />

    <script>
        document.getElementById('registerForm').addEventListener('submit', function(e) {
            const pass = document.getElementById('regPassword').value;
            const cpass = document.getElementById('regConfirmPassword').value;
            if (pass !== cpass) {
                e.preventDefault();
                alert("Passwords do not match! Please verify your password.");
                document.getElementById('regConfirmPassword').focus();
            }
        });
    </script>
</body>
</html>
