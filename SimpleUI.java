package app;

import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class SimpleUI {
    SimpleDateFormat printingFormat = new SimpleDateFormat("MM/dd/yyyy");
    DataManager data = new DataManager();
    TypeVerifier verifier = new TypeVerifier(data);
    String helpString = String.join("\n", "quit - Quit Bike System", 
            "help - Print Help",
            "addrp brand level price days - Add Repair Price", 
            "addc firstName lastName - Add Customer",
            "addo customerNumber date brand level comment - Add Order", 
            "addp customerNumber date amount - Add Payment",
            "comp ID completionDate - Mark order ID completed", 
            "printrp - Print Repair Prices",
            "printcnum - Print Clients by client Number", 
            "printcname - Print Clients by client Name",
            "printo - Print Orders", 
            "printp - Print Payments", 
            "printt - Print Transactions",
            "printr - Print Receivables", 
            "prints - Print Statements", 
            "readc filename - Read Commands From Disk File",
            "savebs filename - Save Bike Shop as a file of commands in file filename",
            "restorebs filename - Restore a previously saved Bike Shop from file filename");
    // Types are as follows - 0: String, 1: Int, 2: Float, 3: Brand, 4: Tier, 5:
    Map<String, Types[]> typeLookup = new HashMap<String, Types[]>();
    // used for rnon and rncn
    Integer nextModifier;

    SimpleUI() {
        typeLookup.put("addrp", new Types[] { Types.String, Types.String, Types.Double, Types.Int });
        typeLookup.put("addc", new Types[] { Types.String, Types.String });
        typeLookup.put("addo", new Types[] { Types.Client, Types.Date, Types.Brand, Types.Tier });
        typeLookup.put("addp", new Types[] { Types.Client, Types.Date, Types.Double });
        typeLookup.put("comp", new Types[] { Types.Order, Types.Date });
        typeLookup.put("savebs", new Types[] { Types.String });
        typeLookup.put("restorebs", new Types[] { Types.String });
        typeLookup.put("rnon", new Types[] { Types.Int });
        typeLookup.put("rncn", new Types[] { Types.Int });
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

    private String readLine() {
        return System.console().readLine();
    }

    private void printClients(Collection<Client> clients) {
        final int maxSizeNumber = Math.max((int) (Math.log10(Client.currentClientNumber) + 1), 2);

        println(String.format("%s\t%s", Support.fit("ID", maxSizeNumber), "Name"));

        for (Client client : clients) {
            println(String.format("%s\t%s", Support.fit(String.valueOf(client.clientNumber), maxSizeNumber), client.fullName));
        }
    }

    private Object[] getTypes(Object[] args) {
        if (typeLookup.containsKey(args[0])) {
            Types[] types = typeLookup.get(args[0]);
            int numberArgsDiff = args.length - 1 - types.length;

            if (numberArgsDiff < 0)
                Support.setErrorMessage(String.format("Missing %d other parameters.", Math.abs(numberArgsDiff)));
            else
                return verifier.getTypes((String[]) args, typeLookup.get(args[0]));
        }
        else
            return args;
        return null;
    }

    private void loadData() {
        println("LOADING STORE " + data.currentStore);
        for (String command : data.commandLog) {
            println(command);
            String[] parts = Support.splitStringIntoParts(command);
            Object[] types = verifier.getTypes(parts, typeLookup.get(parts[0]));

            if (wasError()) {
                println(Support.getErrorMessage() + "\nPress enter to acknowledge.");
                readLine();
                continue;
            }

            executeCmd(types);
        }
        print("FINISHED LOADING\n\n");
    }

    // Analysis reports
    final String lineSeperator = new String(new char[40]).replace("\0", "=");
    private void printStatement(Client c) {
        StringBuilder desc = new StringBuilder();
        desc.append(String.format("Statement for %s\n%s\n", c.fullName, lineSeperator));
        desc.append(String.format("%s\tID\tCHARGE\tCREDIT\tDESC\n", Support.fit("DATE", Support.DATE_LENGTH)));

        for (Order order : c.orders) {
            desc.append(String.format("%s\t%s\t%s\t%s\t%s TIER FOR %s\n", Support.fit(Support.dateToString(order.date), Support.DATE_LENGTH), Support.fit("O" + String.valueOf(order.ID), data.orderNumberSize), Support.fit(String.format("%.2f", order.transactionAmount), data.paymentAmountSize), Support.fit("", data.paymentAmountSize), order.tier.toUpperCase(), order.brand.toUpperCase()));
        }
        for (Payment payment : c.payments) {
            desc.append(String.format("%s\t%s\t%s\t%s\tDEPOSIT\n", Support.fit(Support.dateToString(payment.date), Support.DATE_LENGTH), Support.fit("P" + String.valueOf(payment.ID), data.orderNumberSize), Support.fit("", data.paymentAmountSize), Support.fit(String.format("%.2f", payment.transactionAmount), data.paymentAmountSize)));
        }
        desc.append(String.format("%s\t\nBALANCE: %.2f\n", lineSeperator, c.outstandingAmount));
        println(desc.toString());
    }

    private boolean executeCmd(Object[] args) {
        switch ((String) args[0]) {
            case "help":
                println(helpString);
                break;
            case "addrp":
                data.addRepairPrice(new RepairPrice((String) args[1], (String) args[2], (double) args[3], (int) args[4]));
                println(String.format("Added %s as a new repair price!", args[1]));
                break;
            case "addc":
                Client newClient;
                if (nextModifier == null)
                    newClient = new Client(Support.capitalizeFirstLetter((String) args[1]), Support.capitalizeFirstLetter((String) args[2]));
                else {
                    newClient = new Client(Support.capitalizeFirstLetter((String) args[1]), Support.capitalizeFirstLetter((String) args[2]), nextModifier);
                    nextModifier = null;
                }

                data.addClient(newClient);

                println(String.format("Added client %s %s (ID: %d) to the database.", args[1], args[2],
                        newClient.clientNumber));
                break;
            case "addo":
                // Test if a comment was provided
                Order newOrder = null;
                // If order has a comment/specified ID
                if (args.length >= 6) {
                    String comment = String.join(" ", Arrays.copyOfRange(args, 5, args.length - 1, String[].class));
                    if (nextModifier == null)
                        newOrder = new Order((Client) args[1], (Date) args[2], (String) args[3], (String) args[4], comment);
                    else
                        newOrder = new Order((Client) args[1], (Date) args[2], (String) args[3], (String) args[4], comment, nextModifier);
                }
                else {
                    if (nextModifier == null)
                        newOrder = new Order((Client) args[1], (Date) args[2], (String) args[3], (String) args[4]);
                    else
                        newOrder = new Order((Client) args[1], (Date) args[2], (String) args[3], (String) args[4], nextModifier);
                }

                data.addOrder(newOrder.client, newOrder);

                println(String.format("Added an order for a %s tuneup on type %s (ID: %d) to the database.", args[4],
                        args[3], newOrder.ID));
                break;
            case "addp": {
                Client client = (Client) args[1];
                Payment newPayment = new Payment(client, (Date) args[2], (double) args[3]);
                data.addPayment(client, newPayment);
                println(String.format("Added a $%.2f payment (ID: %d) for %s %s (ID: %d)", args[3],
                        newPayment.ID, client.firstName, client.lastName, client.clientNumber));
            }
                break;
            case "comp":
                Order order = (Order) args[1];
                order.markComplete((Date) args[2]);
                println(String.format("%s's order for a %s tuneup on type %s (ID: %d) marked complete on %s.",
                        order.client.firstName, order.tier, order.brand, order.ID,
                        printingFormat.format(order.completionDate)));
                break;
            case "printo": {
                if (data.orders.size() == 0) {println("No orders found."); break;}

                Collection<Order> orders = data.getOrdersByDate();

                println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\tCOMMENT", Support.fit("ID", data.orderNumberSize), Support.fit("CID", data.clientNumberSize), Support.fit("NAME", data.tierSize), Support.fit("BRAND", data.brandSize), Support.fit("TIER", data.tierSize), Support.fit("DATE", Support.DATE_LENGTH), Support.fit("COMPLETE", Support.DATE_LENGTH), Support.fit("PRICE", data.paymentAmountSize)));

                for (Order o : orders) {
                    println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", Support.fit(String.valueOf(o.ID), data.orderNumberSize), Support.fit(String.valueOf(o.client.clientNumber), data.clientNumberSize), Support.fit(o.client.fullName, data.clientNameSize), Support.fit(o.brand, data.brandSize), Support.fit(o.tier, data.tierSize), Support.fit(Support.dateToString(o.date), Support.DATE_LENGTH), Support.fit(o.completionDate == null ? "" : Support.dateToString(o.completionDate), Support.DATE_LENGTH), Support.fit(String.format("%.2f", o.transactionAmount), data.paymentAmountSize), o.comment == null ? "" : o.comment));                }
                }
                break;
            case "printcname":
                if (data.clients.size() == 0) {println("No clients found."); break;}
                printClients(data.getClientsByName());
                break;
            case "printcnum":
                if (data.clients.size() == 0) {println("No clients found."); break;}
                printClients(data.getClientsById());
                break;
            case "printrp": {
                if (Prices.rps.size() == 0) {println("No orders found."); break;}

                println(String.format("%s\t%s\tprice", Support.fit("BRANDS", data.brandSize), Support.fit("TIER", data.tierSize)));

                for (RepairPrice rp : Prices.rps) {
                    println(String.format("%s\t%s\t%.2f", Support.fit(rp.brand, data.brandSize), Support.fit(rp.tier, data.tierSize), rp.price));
                }
                break;
            }
            case "printp": {
                    if (data.payments.size() == 0) {println("No payments found."); break;}

                    println(String.format("%s\t%s\t%s\tdate", Support.fit("ID", data.paymentNumberSize), Support.fit("CID", data.clientNumberSize), Support.fit("AMOUNT", data.paymentAmountSize)));

                    for (Payment p : data.getAllPayments()) {
                        println(String.format("%s\t%s\t%s\t%s", Support.fit(String.valueOf(p.ID), data.paymentNumberSize), Support.fit(String.valueOf(p.client.clientNumber), data.clientNumberSize), Support.fit(String.format("%.2f", p.transactionAmount), data.paymentAmountSize), Support.fit(Support.dateToString(p.date), Support.DATE_LENGTH)));
                    }
                }

                break;
            case "printt": {
                StringBuilder desc = new StringBuilder();
                println(String.format("%s\t%s\t%s\t%s\tAMOUNT", Support.fit("CID", data.clientNumberSize), Support.fit("NAME", data.clientNameSize), Support.fit("TYPE", 7), Support.fit("DATE", Support.DATE_LENGTH)));

                for (Transaction transaction : data.getTransactionsByDate()) {
                    String type = transaction instanceof Order ? "ORDER" : "PAYMENT";
                    println(String.format("%s\t%s\t%s\t%s\t%s", Support.fit(String.valueOf(transaction.client.clientNumber), data.clientNumberSize), Support.fit(transaction.client.fullName, data.clientNameSize), type, Support.fit(Support.dateToString(transaction.date), Support.DATE_LENGTH), Support.fit(String.format("%.2f", transaction.transactionAmount), data.paymentAmountSize)));
                }

                println(desc.toString());
            }
                break;
            case "printr": {
                println(String.format("%s\t%s\t%s\tLAST PAYMENT DATE", Support.fit("CID", data.clientNumberSize), Support.fit("NAME", data.clientNameSize), Support.fit("OWED", data.paymentAmountSize)));

                for (Client client : data.getAllClients()) {
                    if (client.outstandingAmount == 0) continue;
                    Payment lastPayment = client.getLastPayment();
                    println(String.format("%s\t%s\t%s\t%s", Support.fit(String.valueOf(client.clientNumber), data.clientNumberSize), Support.fit(client.fullName, data.clientNameSize), Support.fit(String.valueOf(client.outstandingAmount), data.paymentAmountSize), Support.fit(lastPayment == null ? "" : Support.dateToString(lastPayment.date), Support.DATE_LENGTH)));
                }
            }
                break;
            case "prints":
                for (Client client : data.getAllClients()) {
                    printStatement(client);
                }
                break;
            case "readc":
                if (!data.loadStore((String) args[1], false))
                    println("Invalid file name!");
                else
                    loadData();
                break;
            case "savebs": {
                String storeName = (String) args[1];
                data.saveStore(storeName);
                println(String.format("Shop successfully saved as '%s'!", storeName));
                break;
            }
            case "restorebs":
                if (!data.loadStore((String) args[1], true))
                    println("Invalid shop name!");
                else
                    loadData();
                break;  
            case "rnon":
            case "rncn":
                nextModifier = (Integer) args[1];
                break;
            default:
                Support.setErrorMessage(args[0] + " is not a valid command.");
                break;
        }

        return !wasError();
    }

    void run() {
        // Load data & execute commands
        data.startup();
        loadData();

        println("Hello! What would you like to do today?");

        while (true) {
            print("Command...");
            String line = readLine();
            // Verify types
            Object[] args = Support.splitStringIntoParts(line);
            String command = (String) args[0];
            // Check for program exit
            if (command.equals("quit")) {
                print("If you want to save your changes, type a filename, otherwise press enter. ");
                line = readLine();
                if (!line.equals("")) {
                    data.saveStore(line);
                    println("Store saved as " + line);
                }
                print("Goodbye!"); 
                return;
            }

            args = getTypes(args);
            // Check for error
            if (args == null || wasError() || !executeCmd(args)) {
                println(Support.getErrorMessage());
                continue;
            }

            // Add command to the log if changed data
            if (command.startsWith("add") || command.equals("comp"))
                data.addCommand(line);

        }        
    }
}