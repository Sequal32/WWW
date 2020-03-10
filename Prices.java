package app;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Prices
 */
public class Prices {
    static HashMap<String, Price> brands = new HashMap<String, Price>();
    static ArrayList<String> tiers = new ArrayList<String>();

    static void addBrand(String brand) {
        Price newBrand = new Price(brand);
        brands.put(brand, newBrand);
    }

    static void addPriceToTier(String brand, String tier, int price) {
        if (!brands.containsKey(brand))
            return;
        brands.get(brand).addPrice(tier, price);
        tiers.add(tier);
    }

    static Integer getPrice(String brand, String tier) {
        if (!brands.containsKey(brand))
            return null;
        return brands.get(brand).getPrice(tier);
    }
}