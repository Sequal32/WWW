package app;

import java.util.Date;

public class Order extends Transaction {
    static int currentOrderNumber;

    Date promiseDate;
    Date completionDate;
    RepairPrice repairPrice;

    String brand;
    String tier;
    String comment;

    Order(Client client, Date orderDate, String brand, String tier)  {
        this.repairPrice = Prices.getRepairPrice(brand, tier);

        this.brand = brand;
        this.tier = tier;
        this.client = client;
        this.date = orderDate;

        this.ID = ++currentOrderNumber;
        this.transactionAmount = this.repairPrice.price;
    }

    Order(Client client, Date orderDate, String brand, String tier, String comment)  {
        this(client, orderDate, brand, tier);
        this.comment = comment;
    }

    Order(Client client, Date orderDate, String brand, String tier, int ID) {
        this(client, orderDate, brand, tier);
        currentOrderNumber = Math.max(currentOrderNumber, ID);
        this.ID = ID;
    }

    Order(Client client, Date orderDate, String brand, String tier, String comment, int ID) {
        this(client, orderDate, brand, tier, comment);
        currentOrderNumber = Math.max(currentOrderNumber, ID);
        this.ID = ID;
    }

    void markComplete(Date completeDate) {
        this.completionDate = completeDate;
    }
}