package app;

import java.util.Date;

public class Order extends Transaction {
    private static int currentOrderNumber;

    int orderNumber;
    Date promiseDate;
    Date completionDate;
    RepairPrice repairPrice;

    String brand;
    String tier;
    String comment;

    boolean complete = false;

    Order(Client client, Date orderDate, String brand, String tier, String comment)  {
        this.repairPrice = Prices.getRepairPrice(brand, tier);

        this.comment = comment;
        this.brand = brand;
        this.tier = tier;
        this.client = client;
        this.date = orderDate;

        this.orderNumber = ++currentOrderNumber;

        this.transactionAmount = this.repairPrice.price;
    }

    void markComplete() {
        this.completionDate = new Date();
        this.complete = true;
    }
}