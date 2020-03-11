package app;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Prices
 */
public class Prices {
    static ArrayList<RepairPrice> rps = new ArrayList<RepairPrice>();
    static HashMap<String, Boolean> tiers = new HashMap<String, Boolean>();
    static HashMap<String, Boolean> brands = new HashMap<String, Boolean>();

    static void addRepairPrice(String brand, String tier, double price, int days) {
        rps.add(new RepairPrice(brand, tier, price, days));
        brands.put(brand, true);
        tiers.put(tier, true);
    }

    static RepairPrice findRepairPrice(String brand, String tier) {
        for (int i = 0; i < rps.size(); i++) {
            RepairPrice repairPrice = rps.get(i);
            if (repairPrice.brand == brand && repairPrice.tier == tier)
                return repairPrice;
        }
        return null;
    }

    static void removeRepairPrice(String brand, String tier) {
        RepairPrice repairPrice = findRepairPrice(brand, tier);
        if (repairPrice == null)
            return;
        rps.remove(repairPrice);
    }

    static RepairPrice getRepairPrice(String brand, String tier) {
        return findRepairPrice(brand, tier);
    }

    static boolean brandExists(String brand) {
        return brands.containsKey(brand);
    }

    static boolean tierExists(String tier) {
        return tiers.containsKey(tier);
    }
}