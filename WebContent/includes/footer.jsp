<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<footer>
    <div class="container text-center">
        <div class="row align-items-center">
            <div class="col-md-6 text-md-start mb-2 mb-md-0">
                <span class="fw-bold text-white">Multi Banking Transaction System</span> &copy; <%= java.time.Year.now().getValue() %>
                <div class="text-secondary small">Developed with Java Servlets, JSP, JDBC &amp; MySQL</div>
            </div>
            <div class="col-md-6 text-md-end">
                <span class="badge bg-secondary me-2"><i class="bi bi-shield-check me-1"></i> 256-Bit SSL Mocked</span>
                <span class="badge bg-primary"><i class="bi bi-cpu me-1"></i> Dynamic Web Architecture</span>
            </div>
        </div>
    </div>
</footer>

<!-- Bootstrap 5 JS Bundle & Icons -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/script.js"></script>
