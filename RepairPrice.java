package app;

/**
 * RepairPrice
 */
public class RepairPrice {
    String brand;
    String tier;
    double price;
    int daysRequired;

    RepairPrice(String brand, String tier, double price, int daysRequired) {
        this.brand = brand;
        this.tier = tier;
        this.price = price;
        this.daysRequired = daysRequired;
    }
}