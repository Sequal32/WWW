package app;

import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.lang.Math;

class SimpleUI {
    final int PAGE_SIZE = 10;

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
        typeLookup.put("comp", new Types[] { Types.Order });
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

    private boolean promptNextPage(int currentElements, int pageCount) {
        println(String.format("\nPage %d/%d\t[q] to quit [enter] next page", calculatePageCount(currentElements), pageCount + 1));
        String result = readLine();
        return result.strip().equals("");
    }

    private int calculatePageCount(int elements) {
        return (int) Math.ceil((double)elements/PAGE_SIZE);
    }

    private void printClients(Collection<Client> clients) {
        final int maxSizeNumber = Math.max((int) (Math.log10(Client.currentClientNumber) + 1), 2);

        println(String.format("%s\t%s", Support.fit("ID", maxSizeNumber), "Name"));

        int count = 1;
        int pageCount = calculatePageCount(data.clients.size());
        for (Client client : clients) {
            count++;
            println(String.format("%s\t%s", Support.fit(String.valueOf(client.clientNumber), maxSizeNumber), client.fullName));
            if (count % PAGE_SIZE == 0) if (!promptNextPage(count, pageCount)) break;
        }
    }

    private Object[] getTypes(Object[] args) {
        if (typeLookup.containsKey(args[0])) {
            Types[] types = typeLookup.get(args[0]);
            int numberArgsDiff = args.length - 1 - types.length;

            if (numberArgsDiff < 0)
                Support.setErrorMessage(String.format("Missing %d other parameters.", Math.abs(numberArgsDiff)));
            else if (numberArgsDiff > 0)
                Support.setErrorMessage("Too many parameters.");
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
        desc.append("DEBT\t\tREASON\n");

        for (Order order : c.orders) {
            desc.append(String.format("+%.2f\t\t%s tier %s tuneup\n", order.transactionAmount, order.tier.toUpperCase(), order.brand.toUpperCase()));
        }
        for (Payment payment : c.payments) {
            desc.append(String.format("-%.2f\t\tPAID ON %s\n", payment.amount, Support.dateToString(payment.date)));
        }
        desc.append(String.format("%s\nTOTAL: %+.2f", lineSeperator, c.outstandingAmount));
        println(desc.toString());
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
                data.addRepairPrice(new RepairPrice((String) args[1], (String) args[2], (double) args[3], (int) args[4]));
                println(String.format("Added %s as a new repair price!", args[1]));
                break;
            case "addc":
                Client newClient = new Client(Support.capitalizeFirstLetter((String) args[1]), Support.capitalizeFirstLetter((String) args[2]), nextModifier);
                data.addClient(newClient);
                nextModifier = null;

                println(String.format("Added client %s %s (ID: %d) to the database.", args[1], args[2],
                        newClient.clientNumber));
                break;
            case "addo":
                // Test if a comment was provided
                Order newOrder = null;
                if (args.length >= 6)
                    newOrder = new Order((Client) args[1], (Date) args[2], (String) args[3], (String) args[4], (String) args[5], nextModifier);
                else
                    newOrder = new Order((Client) args[1], (Date) args[2], (String) args[3], (String) args[4], nextModifier);

                data.addOrder(newOrder.client, newOrder);
                nextModifier = null;

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
                order.markComplete();
                println(String.format("%s's order for a %s tuneup on type %s (ID: %d) marked complete on %s.",
                        order.client.firstName, order.tier, order.brand, order.ID,
                        printingFormat.format(order.completionDate)));
                break;
            case "printo": {
                if (data.orders.size() == 0) {println("No orders found."); break;}

                Collection<Order> orders = data.getOrdersByDate();

                println(String.format("%s\t%s\t%s\t%s\t%s\t\t%s\tPRICE", Support.fit("CID", data.clientNumberSize), Support.fit("NAME", data.tierSize), Support.fit("BRAND", data.brandSize), Support.fit("TIER", data.tierSize), Support.fit("STATUS", 8), Support.fit("DATE", Support.DATE_LENGTH)));

                int count = 0;
                int pageCount = calculatePageCount(orders.size());
                for (Order o : orders) {
                    String status = o.complete ? "COMPLETE" : "PENDING";
                    println(String.format("%s\t%s\t%s\t%s\t%s\t\t%s\t%.2f", Support.fit(String.valueOf(o.client.clientNumber), data.clientNumberSize), Support.fit(o.client.fullName, data.clientNameSize), Support.fit(o.brand, data.brandSize), Support.fit(o.tier, data.tierSize), Support.fit(status, 8), Support.fit(Support.dateToString(o.date), Support.DATE_LENGTH), o.transactionAmount));
                    if (count % PAGE_SIZE == 0) if (!promptNextPage(count, pageCount)) break;
                    count++;
                }
                
                break;
            }
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

                int count = 1;
                int pageCount = calculatePageCount(Prices.rps.size());
                for (RepairPrice rp : Prices.rps) {
                    count++;
                    println(String.format("%s\t%s\t%.2f", Support.fit(rp.brand, data.brandSize), Support.fit(rp.tier, data.tierSize), rp.price));
                    if (count % PAGE_SIZE == 0) if (!promptNextPage(count, pageCount)) break;
                }
                break;
            }
            case "printp": {
                    if (data.payments.size() == 0) {println("No payments found."); break;}

                    println(String.format("%s\t%s\t%s\tdate", Support.fit("ID", data.paymentNumberSize), Support.fit("CID", data.clientNumberSize), Support.fit("AMOUNT", data.paymentAmountSize)));

                    int count = 1;
                    int pageCount = calculatePageCount(data.payments.size());
                    for (Payment p : data.getAllPayments()) {
                        count++;
                        println(String.format("%s\t%s\t%s\t%s", Support.fit(String.valueOf(p.ID), data.paymentNumberSize), Support.fit(String.valueOf(p.client.clientNumber), data.clientNumberSize), Support.fit(String.format("%.2f", p.amount), data.paymentAmountSize), Support.fit(Support.dateToString(p.date), Support.DATE_LENGTH)));
                        if (count % PAGE_SIZE == 0) if (!promptNextPage(count, pageCount)) break;                  
                    }
                }

                break;
            case "printt": {
                StringBuilder desc = new StringBuilder();
                println(String.format("%s\t%s\t%s\t%s\tAMOUNT", Support.fit("CID", data.clientNumberSize), Support.fit("NAME", data.clientNameSize), Support.fit("TYPE", 7), Support.fit("DATE", Support.DATE_LENGTH)));

                for (Transaction transaction : data.getTransactionsByDate()) {
                    String type = transaction instanceof Order ? "ORDER" : "PAYMENT";
                    println(String.format("%s\t%s\t%s\t%s\t%s", Support.fit(String.valueOf(transaction.client.clientNumber), data.clientNumberSize), Support.fit(transaction.client.fullName, data.clientNameSize), type, Support.fit(Support.dateToString(transaction.date), Support.DATE_LENGTH), Support.fit(String.valueOf(transaction.transactionAmount), data.paymentAmountSize)));
                }

                println(desc.toString());
            }
                break;
            case "printr": {
                println(String.format("%s\t%s\t%s\tLAST PAYMENT DATE", Support.fit("CID", data.clientNumberSize), Support.fit("NAME", data.clientNameSize), Support.fit("OWED", data.paymentAmountSize)));

                int count = 1;

                int pageCount = calculatePageCount(data.clients.size());
                for (Client client : data.getAllClients()) {
                    if (client.outstandingAmount == 0) continue;
                    Payment lastPayment = client.getLastPayment();
                    println(String.format("%s\t%s\t%s\t%s", Support.fit(String.valueOf(client.clientNumber), data.clientNumberSize), Support.fit(client.fullName, data.clientNameSize), Support.fit(String.valueOf(client.outstandingAmount), data.paymentAmountSize), Support.fit(lastPayment == null ? "" : Support.dateToString(lastPayment.date), Support.DATE_LENGTH)));
                    count++;
                    if (count % PAGE_SIZE == 0) if (!promptNextPage(count, pageCount)) break;
                }
            }
                break;
            case "prints":
                for (Client client : data.getAllClients()) {
                    printStatement(client);
                }
                break;
            case "readc":
                if (!data.loadStore((String) args[1], false)) {
                    println("Invalid file name!");
                }
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