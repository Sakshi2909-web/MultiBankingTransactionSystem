/**
 * Multi Banking Transaction System - Client Script
 */

document.addEventListener("DOMContentLoaded", function () {
    // Auto-dismiss alerts after 6 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 6000);
    });

    // Dynamic Balance display when an account is selected in Deposit / Withdraw
    const accountSelect = document.getElementById("accountNumberSelect");
    const balanceDisplay = document.getElementById("selectedAccountBalance");

    if (accountSelect && balanceDisplay) {
        accountSelect.addEventListener("change", function () {
            const selectedOpt = accountSelect.options[accountSelect.selectedIndex];
            const bal = selectedOpt.getAttribute("data-balance");
            if (bal !== null && bal !== undefined) {
                balanceDisplay.textContent = "Available Balance: ₹" + parseFloat(bal).toLocaleString('en-IN', { minimumFractionDigits: 2 });
                balanceDisplay.parentElement.classList.remove("d-none");
            } else {
                balanceDisplay.parentElement.classList.add("d-none");
            }
        });
    }

    // Transfer mode toggle (by Account No vs by Email & Bank)
    const modeAccountRadio = document.getElementById("modeAccount");
    const modeEmailRadio = document.getElementById("modeEmail");
    const accountFieldGroup = document.getElementById("groupAccountMode");
    const emailFieldGroup = document.getElementById("groupEmailMode");

    if (modeAccountRadio && modeEmailRadio && accountFieldGroup && emailFieldGroup) {
        modeAccountRadio.addEventListener("change", function () {
            if (this.checked) {
                accountFieldGroup.classList.remove("d-none");
                emailFieldGroup.classList.add("d-none");
            }
        });

        modeEmailRadio.addEventListener("change", function () {
            if (this.checked) {
                accountFieldGroup.classList.add("d-none");
                emailFieldGroup.classList.remove("d-none");
            }
        });
    }

    // Transactions Table Filter
    const filterInput = document.getElementById("transactionSearch");
    if (filterInput) {
        filterInput.addEventListener("keyup", function () {
            const filter = filterInput.value.toLowerCase();
            const rows = document.querySelectorAll("#transactionsTable tbody tr");
            rows.forEach(function (row) {
                const text = row.textContent.toLowerCase();
                if (text.indexOf(filter) > -1) {
                    row.style.display = "";
                } else {
                    row.style.display = "none";
                }
            });
        });
    }
});

// Helper for quick Demo Fill on Login page
function fillDemoUser(email, password) {
    const emailInput = document.getElementById("loginEmail");
    const passInput = document.getElementById("loginPassword");
    if (emailInput && passInput) {
        emailInput.value = email;
        passInput.value = password;
        // highlight briefly
        emailInput.classList.add("is-valid");
        passInput.classList.add("is-valid");
        setTimeout(() => {
            emailInput.classList.remove("is-valid");
            passInput.classList.remove("is-valid");
        }, 1500);
    }
}

// Print / Export Passbook
function printPassbook() {
    window.print();
}
