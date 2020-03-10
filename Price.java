package app;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Price
 */
public class Price {
    String brand;
    HashMap<String, Integer> prices = new HashMap<String, Integer>();

    Price(String brand) {
        this.brand = brand;
    }

    void addPrice(String tier, int price) {
        prices.put(tier, price);
    }

    Integer getPrice(String tier) {
        if (!prices.containsKey(tier))
            return null;
        return prices.get(tier);
    }
}