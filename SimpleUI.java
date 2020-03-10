package app;

import java.util.HashMap;
import java.util.Map;

class SimpleUI {
    TypeVerifier verifier = new TypeVerifier();
    DataManager data = new DataManager();
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
        "readc filename - Read Commands From Disk File",
        "savebs filename - Save Bike Shop as a file of commands in file filename",
        "restorebs filename - Restore a previously saved Bike Shop from file filename"
        );
    // Types are as follows - 0: String, 1: Int, 2: Float, 3: Brand, 4: Tier, 5:
    Map<String, int[]> typeLookup = new HashMap<String, int[]>();

    SimpleUI() {
        typeLookup.put("addrp", new int[]{0, 0, 2, 1});
        typeLookup.put("addc", new int[]{0, 0});
        typeLookup.put("addo", new int[]{1, 0, 0, 0, 0});
        typeLookup.put("addp", new int[]{1, 0, 2});
        typeLookup.put("comp", new int[]{1, 0});
    }

    private void println(String s) {
        System.out.println(s);
    }

    private void print(String s) {
        System.out.print(s);
    }

    private boolean wasError() {
        boolean wasError = Support.wasError();
        if (!wasError)
            return false;
        println(Support.getErrorMessage());
        Support.clearError();
        return wasError;
    }

    void executeCmd(Object[] args) {
        switch((String) args[0]) {
            case "quit":
                println("Goodbye!");
                break;
            case "help":
                println(helpString);
                break;
            case "addrp":
                
                println("Made new repair order!");
                break;
            case "readc":
                break;              
        }

        wasError();
    }

    void run() {
        println("Hello! What would you like to do today?");
        while (true) {
            print("Command...");
            String line = System.console().readLine();
            // Verify types
            Object[] args = Support.splitStringIntoParts(line);
            if (typeLookup.containsKey(args[0])) {
                int[] types = typeLookup.get(args[0]);

                if (types.length != args.length - 1)
                    Support.setErrorMessage("Not enough parameters.");
                else
                    args = verifier.getTypes((String[]) args, typeLookup.get(args[0]));
            }
            // Check for error
            if (wasError())
                continue;

            executeCmd(args);
        }        
    }
}