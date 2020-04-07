package app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

class SimpleUI {
    boolean runQuiet = false;


    SimpleDateFormat printingFormat = new SimpleDateFormat("MM/dd/yyyy");
    DataManager data = new DataManager();
    TypeVerifier verifier = new TypeVerifier(data);
    String helpString = String.join("\n", "quit - Quit Bike System", "help - Print Help",
            "addrp brand level price days - Add Repair Price", "addc firstName lastName - Add Customer",
            "addo customerNumber date brand level comment - Add Order", "addp customerNumber date amount - Add Payment",
            "comp orderNumber completionDate - Mark order orderNumber completed", "printrp - Print Repair Prices",
            "printcnum - Print Clients by client Number", "printcname - Print Clients by client Name",
            "printo - Print Orders", "printp - Print Payments", "printt - Print Transactions",
            "printr - Print Receivables", "prints - Print Statements", "readc filename - Read Commands From Disk File",
            "savebs filename - Save Bike Shop as a file of commands in file filename",
            "restorebs filename - Restore a previously saved Bike Shop from file filename");
    // Types are as follows - 0: String, 1: Int, 2: Float, 3: Brand, 4: Tier, 5:
    Map<String, Types[]> typeLookup = new HashMap<String, Types[]>();

    SimpleUI() {
        typeLookup.put("addrp", new Types[] { Types.String, Types.String, Types.Double, Types.Int });
        typeLookup.put("addc", new Types[] { Types.String, Types.String });
        typeLookup.put("addo", new Types[] { Types.Client, Types.Date, Types.Brand, Types.Tier, Types.String });
        typeLookup.put("addp", new Types[] { Types.Client, Types.String, Types.Double });
        typeLookup.put("comp", new Types[] { Types.Order });
    }

    private void println(String s) {
        if (runQuiet)
            return;
        System.out.println(s);
    }

    private void print(String s) {
        if (runQuiet)
            return;
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

    private boolean executeCmd(Object[] args) {
        switch ((String) args[0]) {
            case "quit":
                println("Goodbye!");
                break;
            case "help":
                println(helpString);
                break;
            case "addrp":
                Prices.addRepairPrice((String) args[1], (String) args[2], (double) args[3], (int) args[4]);
                println(String.format("Added %s as a new repair price!", args[1]));
                break;
            case "addc":
                Client newClient = new Client(Support.capitalizeFirstLetter((String) args[1]),
                        Support.capitalizeFirstLetter((String) args[2]));
                data.addClient(newClient);
                println(String.format("Added client %s %s (ID: %d) to the database.", args[1], args[2],
                        newClient.clientNumber));
                break;
            case "addo":
                Order newOrder = new Order((Client) args[1], (Date) args[2], (String) args[3], (String) args[4],
                        (String) args[5]);
                data.addOrder(newOrder);
                println(String.format("Added an order for a %s tuneup on type %s (ID: %d) to the database.", args[4],
                        args[3], newOrder.orderNumber));
                break;
            case "addp":
                Client client = (Client) args[1];
                Payment newPayment = new Payment(client, (Double) args[2], (Date) args[3]);
                data.addPayment(newPayment);
                println(String.format("Added a $%.2f payment (ID: %d) for %s %s (ID: %d)", args[2],
                        newPayment.paymentNumber, client.firstName, client.lastName, client.clientNumber));
                break;
            case "comp":
                Order order = (Order) args[1];
                order.markComplete();
                println(String.format("%s's order for a %s tuneup on type %s (ID: %d) marked complete on %s.",
                        order.client.firstName, order.tier, order.brand, order.orderNumber,
                        printingFormat.format(order.completionDate)));
                break;
            case "printo":
                Collection<Order> orders = data.getAllOrders();
                final int maxSizeBrand = Support.getLongestStringSizeGeneric(orders, x -> ((Order) x).brand.length());
                final int maxSizeTier = Support.getLongestStringSizeGeneric(orders, x -> ((Order) x).tier.length());
                final int maxSizeName = Support.getLongestStringSizeGeneric(orders, x -> ((Order) x).client.fullName.length());
                
                
                break;
            case "printcname":
                break;
            case "printcnum":
                break;
            case "printrp":
                final int maxSizeBrand = Support.getLongestStringSize(Prices.brands.keySet());
                final int maxSizeTier = Support.getLongestStringSize(Prices.tiers.keySet());
                println(String.format("%s\t%s\tprice", Support.fit("brands", maxSizeBrand, true), Support.fit("tier", maxSizeTier, true)));
                for (RepairPrice rp : Prices.rps) {
                    println(String.format("%s\t%s\t%.2f", Support.fit(rp.brand, maxSizeBrand, true), Support.fit(rp.tier, maxSizeTier, true), rp.price));
                }
                break;
            case "printp":
                break;
            case "printt":
                break;
            case "printr":
                break;
            case "prints":
                break;
            case "readc":
                break;
            case "savebs":
                break;
            case "restorebs":
                break;              
        }

        return wasError();
    }

    void run() {
        // Load data & execute commands
        data.startup();
        for (String command : data.commandLog) {
            String[] parts = Support.splitStringIntoParts(command);
            executeCmd(verifier.getTypes(parts, typeLookup.get(parts[0])));
        }

        println("Hello! What would you like to do today?");

        while (true) {
            print("Command...");
            String line = System.console().readLine();
            // Verify types
            Object[] args = Support.splitStringIntoParts(line);
            if (typeLookup.containsKey(args[0])) {
                Types[] types = typeLookup.get(args[0]);

                if (types.length < args.length - 1)
                    Support.setErrorMessage("Too little parameters.");
                else if (types.length > args.length - 1)
                    Support.setErrorMessage("Too many parameters.");
                else
                    args = verifier.getTypes((String[]) args, typeLookup.get(args[0]));
            }
            // Check for error
            if (wasError())
                continue;

            if (executeCmd(args)) {
                data.addCommand(line);
            };
        }        
    }
}