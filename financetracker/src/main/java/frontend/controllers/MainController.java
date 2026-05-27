package frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

import frontend.models.Transaction;

public class MainController implements Initializable {

    // ── Navigation Buttons ─────────────────────────────────────────
    @FXML
    private Button dashboardBtn;
    @FXML
    private Button addTransactionBtn;
    @FXML
    private Button historyBtn;

    // ── Global Search ──────────────────────────────────────────────
    @FXML
    private TextField searchField;

    // ── Modular included pages (referenced by fx:id in main-layout.fxml)
    @FXML
    private Region dashboardPage;
    @FXML
    private Region addTransactionPage;
    @FXML
    private Region historyPage;

    // ── Associated child controllers (named: fx:id + "Controller")
    @FXML
    private DashboardController dashboardPageController;
    @FXML
    private AddTransactionController addTransactionPageController;
    @FXML
    private HistoryController historyPageController;

    // ── Lifecycle ─────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Wire up main controller references to child pages
        if (dashboardPageController != null) {
            dashboardPageController.setMainController(this);
        }
        if (addTransactionPageController != null) {
            addTransactionPageController.setMainController(this);
        }

        // Connect the global topbar search directly to the History page search
        // predicate
        if (searchField != null && historyPageController != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                historyPageController.setSearchQuery(newVal);
                // If user starts typing search query, auto-navigate to History page to display
                // results
                if (newVal != null && !newVal.trim().isEmpty() && historyPage != null && !historyPage.isVisible()) {
                    navigateToHistory();
                }
            });
        }

        // Render Dashboard as default visible view
        navigateToDashboard();
    }

    // ── Shell Navigation Methods ──────────────────────────────────

    @FXML
    private void handleNavigation(ActionEvent event) {
        Button clicked = (Button) event.getSource();

        if (clicked == dashboardBtn) {
            navigateToDashboard();
        } else if (clicked == addTransactionBtn) {
            navigateToAddTransaction();
        } else if (clicked == historyBtn) {
            navigateToHistory();
        }
    }

    public void navigateToDashboard() {
        showPage(dashboardPage, dashboardBtn);
    }

    public void navigateToHistory() {
        showPage(historyPage, historyBtn);
    }

    public void navigateToAddTransaction() {
        showPage(addTransactionPage, addTransactionBtn);
    }

    public void goToEditTransaction(Transaction trx) {
        if (addTransactionPageController != null) {
            addTransactionPageController.setTransactionToEdit(trx);
            navigateToAddTransaction();
        } else {
            System.out.println("Error: Controller Add Transaction belum siap!");
        }
    }

    /**
     * Toggles visibility and managed properties, updating button styles.
     */
    private void showPage(Region targetPage, Button targetBtn) {
        if (targetPage == null || targetBtn == null)
            return;

        // Reset all views
        setPageActive(dashboardPage, false);
        setPageActive(addTransactionPage, false);
        setPageActive(historyPage, false);

        // Reset active sidebar buttons
        dashboardBtn.getStyleClass().remove("active");
        addTransactionBtn.getStyleClass().remove("active");
        historyBtn.getStyleClass().remove("active");

        // Focus and activate the selected target
        setPageActive(targetPage, true);
        if (!targetBtn.getStyleClass().contains("active")) {
            targetBtn.getStyleClass().add("active");
        }
    }

    private void setPageActive(Region page, boolean isActive) {
        if (page != null) {
            page.setVisible(isActive);
            page.setManaged(isActive);
        }
    }
}