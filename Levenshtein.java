package app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * Levenshtein
 */
public class Levenshtein {
    static int THRESHOLD = 2;

    // from https://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
    // Written by us
    public static <T> Collection<T> getBestMatchesOnObject(String a, Collection<T> searches, Function<T, String> stringGetter) {
        HashMap<T, Integer> result = new HashMap<T, Integer>();

        for (T object : searches) {
            int diff = distance(a, stringGetter.apply(object));
            if (diff < THRESHOLD)
                result.put(object, diff);
        }

        ArrayList<Entry<T, Integer>> values = new ArrayList<>(result.entrySet());
        values.sort(Entry.comparingByValue());

        ArrayList<T> returnResult = new ArrayList<T>();
        for (Entry<T, Integer> entry : values) {
            returnResult.add(entry.getKey());
        }

        return returnResult;
    }
}