package databasesystems.project2;

import java.util.Date; // Import the Date class

public class Order {
    private int orderId;
    private String customerName;
    private String status;
    private double totalCost;
    private Date orderDate; // Define orderDate variable

    public Order(int orderId, String customerName, String status, double totalCost, Date orderDate) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.status = status;
        this.totalCost = totalCost;
        this.orderDate = orderDate;
    }

    public int getOrderId() {
        return this.orderId; // Fixed name from orderID to orderId
    }

    public Date getOrderDate() {
        return this.orderDate;
    }

    public double getTotal() {
        return this.totalCost;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getStatus() {
        return status;
    }

    public double getTotalCost() {
        return totalCost;
    }
}
