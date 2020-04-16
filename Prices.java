package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
/**
 * Prices
 */
public class Prices {
    static ArrayList<RepairPrice> rps = new ArrayList<RepairPrice>();
    static HashSet<String> tiers = new HashSet<String>();
    static HashSet<String> brands = new HashSet<String>();

    static void addRepairPrice(RepairPrice rp) {
        rps.add(rp);
        brands.add(rp.brand);
        tiers.add(rp.tier);
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
        return brands.contains(brand);
    }

    static boolean tierExists(String tier) {
        return tiers.contains(tier);
    }
}