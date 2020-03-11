package app;

import java.util.Date;

public class Order {
    private static int currentOrderNumber;

    int orderNumber;
    Date orderDate;
    Date promiseDate;
    Date completionDate;
    RepairPrice repairPrice;

    String brand;
    String tier;
    String comment;

    Client client;

    boolean complete = false;

    Order(Client client, Date date, String brand, String tier, String comment)  {
        this.repairPrice = Prices.getRepairPrice(brand, tier);
        this.orderDate = date;

        this.comment = comment;
        this.brand = brand;
        this.tier = tier;
        this.client = client;

        this.orderNumber = ++currentOrderNumber;
    }

    void markComplete() {
        this.completionDate = new Date();
        this.complete = true;
    }
}