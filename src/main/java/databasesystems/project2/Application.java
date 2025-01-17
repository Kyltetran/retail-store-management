package databasesystems.project2;

public class Application {
    public void showDashboard(User user) {
        System.out.println("Welcome, " + user.getUsername());

        if ("buyer".equals(user.getRole())) {
            // Show buyer's dashboard
            showBuyerDashboard();
        } else if ("seller".equals(user.getRole())) {
            // Show seller's dashboard
            showSellerDashboard();
        } else if ("manager".equals(user.getRole())) {
            // Show manager's dashboard
            showManagerDashboard();
        } else {
            System.out.println("Invalid user role: " + user.getRole());
        }
    }

    private void showBuyerDashboard() {
        System.out.println("Displaying buyer dashboard: browse products, view orders, etc.");
    }

    private void showSellerDashboard() {
        System.out.println("Displaying seller dashboard: manage products, inventory, suppliers, process orders.");
    }

    private void showManagerDashboard() {
        System.out.println("Displaying manager dashboard: view reports, manage employees, etc.");
    }
}
