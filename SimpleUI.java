package app;

import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.lang.Math;

class SimpleUI {
    boolean runQuiet = false;
    final int PAGE_SIZE = 10;

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

    private String readLine() {
        return System.console().readLine();
    }

    private boolean promptNextPage(int currentElements, int pageCount) {
        println(String.format("\nPage %d/%d\t[q] to quit [enter] next page", calculatePageCount(currentElements), pageCount + 1));
        String result = readLine();
        return result.strip().equals("");
    }

    private int calculatePageCount(int elements) {
        return (int) Math.ceil((double)elements/PAGE_SIZE);
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
            case "printo": {
                Collection<Order> orders = data.getAllOrders();

                if (orders.size() == 0) {println("No orders found."); break;}

                final int maxSizeBrand = Support.getLongestStringSizeGeneric(orders, x -> ((Order) x).brand.length());
                final int maxSizeTier = Support.getLongestStringSizeGeneric(orders, x -> ((Order) x).tier.length());
                final int maxSizeName = Support.getLongestStringSizeGeneric(orders, x -> ((Order) x).client.fullName.length());

                println(String.format("%s\t%s\t%s\t%s\tClient ID\tprice", Support.fit("brands", maxSizeBrand, true), Support.fit("tier", maxSizeTier, true), Support.fit("Client Name", maxSizeName, true)));

                int count = 0;
                int pageCount = calculatePageCount(orders.size());
                for (Order o : orders) {
                    println(String.format("%s\t%s\t%s\t%.2f", Support.fit(o.brand, maxSizeBrand, true), Support.fit(o.tier, maxSizeTier, true), Support.fit(o.client.fullName, maxSizeTier, true), o.repairPrice));
                    if (count % PAGE_SIZE == 0) if (!promptNextPage(count, pageCount)) break;
                    count++;
                }
                
                break;
            }
            case "printcname":
                break;
            case "printcnum":
                break;
            case "printrp": {
                if (Prices.rps.size() == 0) {println("No orders found."); break;}

                final int maxSizeBrand = Support.getLongestStringSize(Prices.brands.keySet());
                final int maxSizeTier = Support.getLongestStringSize(Prices.tiers.keySet());
                println(String.format("%s\t%s\tprice", Support.fit("brands", maxSizeBrand, true), Support.fit("tier", maxSizeTier, true)));

                int count = 1;
                int pageCount = calculatePageCount(Prices.rps.size());
                for (RepairPrice rp : Prices.rps) {
                    count++;
                    println(String.format("%s\t%s\t%.2f", Support.fit(rp.brand, maxSizeBrand, true), Support.fit(rp.tier, maxSizeTier, true), rp.price));
                    if (count % PAGE_SIZE == 0) if (!promptNextPage(count, pageCount)) break;
                }
                break;
            }
            case "printp":
                break;
            case "printt":
                break;
            case "printr":
                break;
            case "prints":
                break;
            case "readc":
                if (!data.loadStore((String) args[0], false)) {
                    println("Invalid file name!");
                }
                break;
            case "savebs": {
                String storeName = (String) args[0];
                data.saveStore(storeName);
                println(String.format("Shop successfully saved as '%s'!", storeName));
                break;
            }
            case "restorebs":
                if (!data.loadStore((String) args[0], true)) {
                    println("Invalid shop name!");
                }
                break;              
        }

        return wasError();
    }

    void run() {
        // Avoid printing initial data to the console
        runQuiet = true;
        // Load data & execute commands
        data.startup();
        for (String command : data.commandLog) {
            String[] parts = Support.splitStringIntoParts(command);
            executeCmd(verifier.getTypes(parts, typeLookup.get(parts[0])));
        }
        runQuiet = false;

        println("Hello! What would you like to do today?");

        while (true) {
            print("Command...");
            String line = readLine();
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