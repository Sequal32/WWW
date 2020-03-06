package app;

import java.util.HashMap;
import java.util.Map;

class SimpleUI {
    Support support = new Support();
    TypeVerifier verifier = new TypeVerifier(support);
    String helpString = String.join("\n",
        "quit - Quit Bike System",
        "help - Print Help",
        "addrp brand level price days - Add Repair Price",
        "addc firstName lastName - Add Customer",
        "addo customerNumber date brand level comment - Add Order",
        "addp customerNumber date amount - Add Payment",
        "comp orderNUmber completionDate - Mark order orderNumber completed",
        "printrp - Print Repair Prices",
        "printcnum - Print Customers by Customer Number",
        "printcname - Print Cusstomers by Customer Name",
        "printo - Print Orders",
        "printp - Print Payments",
        "printt - Print Transactions",
        "printr - Print Receivables",
        "prints - Print Statements",
        "readc filename - Read Commands From Disk File filename",
        "savebs filename - Save Bike Shop as a file of commands in file filename",
        "restorebs filename - Restore a previously saved Bike Shop from file filename"
        );
    // Types are as follows - 0: String, 1: Int, 2: Float, 3: 
    Map<String, int[]> typeLookup = new HashMap<String, int[]>();

    SimpleUI() {
        typeLookup.put("addrp", new int[]{0, 0, 2, 1});
        typeLookup.put("addc", new int[]{0, 0});
        typeLookup.put("addo", new int[]{1, 0, 0, 0, 0});
        typeLookup.put("addp", new int[]{1, 0, 2});
        typeLookup.put("comp", new int[]{1, 0});
    }

    private void print(String s) {
        System.out.println(s);
    }

    void run() {
        print("Hello! What would you like to do today?");
        while (true) {
            print("Command...");
            String line = System.console().readLine();
            // Verify types
            Object[] args = support.splitStringIntoParts(line);
            if (typeLookup.containsKey(args[0]))
                args = verifier.getTypes((String[]) args, typeLookup.get(args[0]));
            // Check for error
            if (support.wasError()) {
                print(support.getErrorMessage());
                support.clearError();
                continue;
            }

            switch(line) {
                case "quit":
                    print("Goodbye!");
                    break;
                case "help":
                    print(helpString);
                    break;
                case "addrp":
                    break;
            }
        }        
    }
}