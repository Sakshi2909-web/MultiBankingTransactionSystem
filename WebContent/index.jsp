<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% request.setAttribute("activePage", "home"); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Multi Banking Transaction System - Home</title>
    <!-- Bootstrap 5 CSS & Bootstrap Icons & Google Font -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

    <!-- Header Navbar -->
    <jsp:include page="/includes/navbar.jsp" />

    <main>
        <!-- Hero Section -->
        <section class="py-5" style="background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%); color: white;">
            <div class="container py-4">
                <div class="row align-items-center g-5">
                    <div class="col-lg-7">
                        <span class="badge bg-primary px-3 py-2 rounded-pill mb-3 text-uppercase fw-bold letter-spacing-1">
                            <i class="bi bi-shield-lock-fill me-1"></i> Next-Gen Multi-Banking
                        </span>
                        <h1 class="display-4 fw-bold lh-sm mb-3">
                            One Unified Platform For All Your <span style="color: #60a5fa;">Bank Accounts</span>
                        </h1>
                        <p class="lead text-light opacity-75 mb-4">
                            Seamlessly link and manage multiple accounts across SBI, HDFC, ICICI, Axis and PNB. 
                            Perform instant deposits, withdrawals, and inter-bank transfers with real-time audit passbook.
                        </p>
                        <div class="d-flex flex-wrap gap-3">
                            <a href="<%= request.getContextPath() %>/register.jsp" class="btn btn-primary btn-lg px-4 shadow">
                                <i class="bi bi-person-plus-fill me-2"></i> Open An Account
                            </a>
                            <a href="<%= request.getContextPath() %>/login.jsp" class="btn btn-outline-light btn-lg px-4">
                                <i class="bi bi-box-arrow-in-right me-2"></i> Customer Login
                            </a>
                        </div>
                    </div>
                    <div class="col-lg-5 text-center">
                        <div class="bank-atm-card card-sbi text-start mx-auto mb-3" style="max-width: 380px;">
                            <div class="d-flex justify-content-between align-items-center">
                                <span class="fw-bold fs-5">STATE BANK OF INDIA</span>
                                <i class="bi bi-wifi fs-4"></i>
                            </div>
                            <div class="card-chip my-3"></div>
                            <div class="card-acc-num">SBIN •••• 1001</div>
                            <div class="d-flex justify-content-between align-items-end mt-2">
                                <div>
                                    <small class="text-white-50 text-uppercase d-block" style="font-size: 0.7rem;">Card Holder</small>
                                    <span class="fw-semibold">RAHUL SHARMA</span>
                                </div>
                                <div class="text-end">
                                    <small class="text-white-50 text-uppercase d-block" style="font-size: 0.7rem;">Balance</small>
                                    <span class="fs-5 fw-bold">₹25,000.00</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Supported Banks Section -->
        <section class="py-4 bg-white border-bottom">
            <div class="container text-center">
                <span class="text-muted small text-uppercase fw-bold letter-spacing-1">Supported Partner Banks</span>
                <div class="row row-cols-2 row-cols-md-5 g-3 align-items-center justify-content-center mt-2">
                    <div class="col"><span class="badge bg-light text-dark p-2 w-100 border fs-6"><i class="bi bi-bank text-primary me-2"></i>SBI</span></div>
                    <div class="col"><span class="badge bg-light text-dark p-2 w-100 border fs-6"><i class="bi bi-bank text-danger me-2"></i>HDFC</span></div>
                    <div class="col"><span class="badge bg-light text-dark p-2 w-100 border fs-6"><i class="bi bi-bank text-warning me-2"></i>ICICI</span></div>
                    <div class="col"><span class="badge bg-light text-dark p-2 w-100 border fs-6"><i class="bi bi-bank text-danger-emphasis me-2"></i>AXIS</span></div>
                    <div class="col"><span class="badge bg-light text-dark p-2 w-100 border fs-6"><i class="bi bi-bank text-success me-2"></i>PNB</span></div>
                </div>
            </div>
        </section>

        <!-- Key Features Section -->
        <section class="py-5">
            <div class="container">
                <div class="text-center mb-5">
                    <h2 class="fw-bold">Why Multi Banking System?</h2>
                    <p class="text-muted">A robust, dynamic banking portal engineered with Java EE, Servlets, and JDBC</p>
                </div>

                <div class="row g-4">
                    <div class="col-md-4">
                        <div class="bank-card p-4 h-100">
                            <div class="stat-icon icon-blue mb-3">
                                <i class="bi bi-wallet2"></i>
                            </div>
                            <h5 class="fw-bold">Multi-Account Linking</h5>
                            <p class="text-muted small">Open and maintain separate accounts across top Indian national and private banks under a single customer identity.</p>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="bank-card p-4 h-100">
                            <div class="stat-icon icon-green mb-3">
                                <i class="bi bi-arrow-left-right"></i>
                            </div>
                            <h5 class="fw-bold">Instant Fund Transfer</h5>
                            <p class="text-muted small">Transfer money seamlessly between your own accounts or to other customers with real-time balance validation and atomic transactions.</p>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="bank-card p-4 h-100">
                            <div class="stat-icon icon-purple mb-3">
                                <i class="bi bi-journal-text"></i>
                            </div>
                            <h5 class="fw-bold">Digital Passbook</h5>
                            <p class="text-muted small">Track every credit and debit with complete timestamping, transaction reference IDs, and printable mini-statement filters.</p>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </main>

    <!-- Footer -->
    <jsp:include page="/includes/footer.jsp" />

</body>
</html>
