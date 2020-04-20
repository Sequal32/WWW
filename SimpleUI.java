package app;

import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class SimpleUI {
    SimpleDateFormat printingFormat = new SimpleDateFormat("MM/dd/yyyy");
    DataManager data = new DataManager();
    TypeVerifier verifier = new TypeVerifier(data);
    String helpString = String.join("\n", "quit - Quit Bike System", 
            "help - Print Help",
            "\nADDING TO THE DATABASE",
            "addrp brand level price days - Add Repair Price", 
            "addc firstName lastName - Add Customer",
            "addo customerNumber date brand level comment - Add Order", 
            "addp customerNumber date amount - Add Payment",
            "comp ID completionDate - Mark order ID completed", 
            "\nGETTING INFORMATION",
            "printrp - Print Repair Prices",
            "printcnum - Print Clients by client Number", 
            "printcname - Print Clients by client Name",
            "printo - Print Orders", 
            "printp - Print Payments", 
            "printt - Print Transactions",
            "printr - Print Receivables", 
            "prints - Print Statements", 
            "\nLOOKING UP DATA",
            "lookupl lastName - Looks up clients by the given lastName",
            "lookupf firstName - Looks up clients by the given firstName",
            "geto - Gets an order by a order id",
            "getp - Gets an payment by a payment id",
            "getc - Gets an client by a client id",
            "\nDATA MANAGEMENT",
            "readc filename - Read Commands From Disk File",
            "savebs filename - Save Bike Shop as a file of commands in file filename",
            "restorebs filename - Restore a previously saved Bike Shop from file filename");
    Map<String, Types[]> typeLookup = new HashMap<String, Types[]>();
    // used for rnon and rncn
    Integer nextClientModifier;
    Integer nextOrderModifier;
    Integer nextPaymentModifier;
    boolean loading = false;
    String toAdd;

    SimpleUI() {
        typeLookup.put("addrp", new Types[] { Types.String, Types.String, Types.Float, Types.Int });
        typeLookup.put("addc", new Types[] { Types.String, Types.String });
        typeLookup.put("addo", new Types[] { Types.Client, Types.Date, Types.Brand, Types.Tier });
        typeLookup.put("addp", new Types[] { Types.Client, Types.Date, Types.Float });
        typeLookup.put("comp", new Types[] { Types.Order, Types.Date });
        typeLookup.put("savebs", new Types[] { Types.String });
        typeLookup.put("restorebs", new Types[] { Types.String });
        typeLookup.put("rnon", new Types[] { Types.Int });
        typeLookup.put("rncn", new Types[] { Types.Int });
        typeLookup.put("rnpn", new Types[] { Types.Int });
        typeLookup.put("lookupcl", new Types[] { Types.String });
        typeLookup.put("lookupcf", new Types[] { Types.String });
        typeLookup.put("geto", new Types[] { Types.String });
        typeLookup.put("getp", new Types[] { Types.String });
        typeLookup.put("getc", new Types[] { Types.String });
        typeLookup.put("printcinfo", new Types[] { Types.Client });
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
        loading = true;
        for (String command : data.commandLog) {
            println(command);
            String[] parts = Support.splitStringIntoParts(command);
            Object[] types = verifier.getTypes(parts, typeLookup.get(parts[0]));

            if (wasError()) {
                println("\n" + Support.getErrorMessage() + "Press enter to acknowledge.");
                readLine();
                continue;
            }

            executeCmd(types);
        }
        loading = false;
        print("FINISHED LOADING\n\n");
    }

    // Analysis reports
    final String lineSeperator = new String(new char[40]).replace("\0", "=");
    private void printStatement(Client c) {
        println(String.format("Statement for %s\n%s", c.fullName, lineSeperator));
        println(String.format("%s\tID\tCHARGE\tCREDIT\tDESC", Support.fit("DATE", Support.DATE_LENGTH)));

        for (Transaction transaction : data.getTransactionsByDate(c)) {
            if (transaction instanceof Order) {
                Order order = (Order) transaction;
                println(String.format("%s\t%s\t%s\t%s\t%s TIER FOR %s", Support.fit(Support.dateToString(order.date), Support.DATE_LENGTH), Support.fit("O" + String.valueOf(order.ID), data.orderNumberSize), Support.fit(String.format("%.2f", order.transactionAmount), data.paymentAmountSize), Support.fit("", data.paymentAmountSize), order.tier.toUpperCase(), order.brand.toUpperCase()));
            }
            else {
                Payment payment = (Payment) transaction;
                println(String.format("%s\t%s\t%s\t%s\tDEPOSIT", Support.fit(Support.dateToString(payment.date), Support.DATE_LENGTH), Support.fit("P" + String.valueOf(payment.ID), data.orderNumberSize), Support.fit("", data.paymentAmountSize), Support.fit(String.format("%.2f", payment.transactionAmount), data.paymentAmountSize)));
            }
        }

        println(String.format("%s\t\nBALANCE: %.2f\n", lineSeperator, c.outstandingAmount));
    }

    private void printReceivables(Collection<Client> clients) {
        println(String.format("%s\t%s\t%s\t%s\tLAST PAYMENT DATE", Support.fit("CID", data.clientNumberSize), Support.fit("NAME", data.clientNameSize), Support.fit("OWED", data.orderAmountSize), Support.fit("PAID", data.paymentAmountSize)));

        for (Client client : clients) {
            if (client.outstandingAmount == 0) continue;
            Payment lastPayment = client.getLastPayment();
            println(String.format("%s\t%s\t%s\t%s\t%s", Support.fit(String.valueOf(client.clientNumber), data.clientNumberSize), Support.fit(client.fullName, data.clientNameSize), Support.fit(String.valueOf(client.outstandingAmount), data.getDigits((int) client.outstandingAmount) + 3), Support.fit(String.format("%.2f", client.totalPaid), data.getDigits((int) client.totalPaid) + 3), Support.fit(lastPayment == null ? "" : Support.dateToString(lastPayment.date), Support.DATE_LENGTH)));
        }
    }

    private void printClients(Collection<Client> clients) {
        final int maxSizeNumber = Math.max((int) (Math.log10(Client.currentClientNumber) + 1), 2);

        println(String.format("%s\t%s", Support.fit("ID", maxSizeNumber), "Name"));

        for (Client client : clients) {
            println(String.format("%s\t%s", Support.fit(String.valueOf(client.clientNumber), maxSizeNumber), client.fullName));
        }
    }

    private void printPayments(Collection<Payment> payments) {
        println(String.format("%s\t%s\t%s\tdate", Support.fit("ID", data.paymentNumberSize), Support.fit("CID", data.clientNumberSize), Support.fit("AMOUNT", data.paymentAmountSize)));

        for (Payment p : payments) {
            println(String.format("%s\t%s\t%s\t%s", Support.fit(String.valueOf(p.ID), data.paymentNumberSize), Support.fit(String.valueOf(p.client.clientNumber), data.clientNumberSize), Support.fit(String.format("%.2f", p.transactionAmount), data.paymentAmountSize), Support.fit(Support.dateToString(p.date), Support.DATE_LENGTH)));
        }
    }

    private void printOrders(Collection<Order> orders) {
        println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\tCOMMENT", Support.fit("ID", data.orderNumberSize), Support.fit("CID", data.clientNumberSize), Support.fit("NAME", data.tierSize), Support.fit("BRAND", data.brandSize), Support.fit("TIER", data.tierSize), Support.fit("DATE", Support.DATE_LENGTH), Support.fit("COMPLETE", Support.DATE_LENGTH), Support.fit("PRICE", data.paymentAmountSize), Support.fit("DAYS LEFT", 9)));

        for (Order o : orders) {
            // Find diff of two dates
            String daysUntilDue = Support.fit(String.valueOf((int) TimeUnit.DAYS.convert((o.date.getTime()-new Date().getTime()), TimeUnit.MILLISECONDS)), Support.DATE_LENGTH);
            println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", Support.fit(String.valueOf(o.ID), data.orderNumberSize), Support.fit(String.valueOf(o.client.clientNumber), data.clientNumberSize), Support.fit(o.client.fullName, data.clientNameSize), Support.fit(o.brand, data.brandSize), Support.fit(o.tier, data.tierSize), Support.fit(Support.dateToString(o.date), Support.DATE_LENGTH), Support.fit(o.completionDate == null ? "" : Support.dateToString(o.completionDate), Support.DATE_LENGTH), Support.fit(String.format("%.2f", o.transactionAmount), data.paymentAmountSize), Support.fit(o.completionDate == null ? daysUntilDue : "", 9), o.comment == null ? "" : o.comment));
        }
    }

    private void printTransactions(Collection<Transaction> transactions) {
        println(String.format("%s\t%s\t%s\t%s\tAMOUNT", Support.fit("CID", data.clientNumberSize), Support.fit("NAME", data.clientNameSize), Support.fit("TYPE", 7), Support.fit("DATE", Support.DATE_LENGTH)));

        for (Transaction transaction : data.getTransactionsByDate()) {
            String type = transaction instanceof Order ? "ORDER" : "PAYMENT";
            println(String.format("%s\t%s\t%s\t%s\t%s", Support.fit(String.valueOf(transaction.client.clientNumber), data.clientNumberSize), Support.fit(transaction.client.fullName, data.clientNameSize), type, Support.fit(Support.dateToString(transaction.date), Support.DATE_LENGTH), Support.fit(String.format("%.2f", transaction.transactionAmount), data.paymentAmountSize)));
        }
    }

    private boolean executeCmd(Object[] args) {
        switch ((String) args[0]) {
            case "help":
                println(helpString);
                break;
            case "addrp":
                data.addRepairPrice(new RepairPrice((String) args[1], (String) args[2], (float) args[3], (int) args[4]));
                println(String.format("Added %s as a new repair price!", args[1]));
                break;
            case "addc":
                Client newClient;
                if (nextClientModifier == null)
                    newClient = new Client(Support.capitalizeFirstLetter((String) args[1]), Support.capitalizeFirstLetter((String) args[2]));
                else {
                    newClient = new Client(Support.capitalizeFirstLetter((String) args[1]), Support.capitalizeFirstLetter((String) args[2]), nextClientModifier);
                    nextClientModifier = null;
                }

                data.addClient(newClient);

                if (!loading)
                    toAdd = "rncn " + Client.currentClientNumber;

                println(String.format("Added client %s %s (ID: %d) to the database.", args[1], args[2],
                        newClient.clientNumber));
                break;
            case "addo":
                // Test if a comment was provided
                Order newOrder = null;
                // If order has a comment/specified ID
                if (args.length >= 6) {
                    String comment = String.join(" ", Arrays.copyOfRange(args, 5, args.length - 1, String[].class));
                    if (nextOrderModifier == null)
                        newOrder = new Order((Client) args[1], (Date) args[2], (String) args[3], (String) args[4], comment);
                    else
                        newOrder = new Order((Client) args[1], (Date) args[2], (String) args[3], (String) args[4], comment, nextOrderModifier);
                }
                else {
                    if (nextOrderModifier == null)
                        newOrder = new Order((Client) args[1], (Date) args[2], (String) args[3], (String) args[4]);
                    else
                        newOrder = new Order((Client) args[1], (Date) args[2], (String) args[3], (String) args[4], nextOrderModifier);
                }
                nextOrderModifier = null;
                data.addOrder(newOrder.client, newOrder);

                if (!loading)
                    toAdd = "rnon " + Order.currentOrderNumber;

                println(String.format("Added an order for a %s tuneup on type %s (ID: %d) to the database.", args[4],
                        args[3], newOrder.ID));
                break;
            case "addp": {
                Client client = (Client) args[1];

                Payment newPayment;
                if (nextPaymentModifier == null)
                    newPayment = new Payment(client, (Date) args[2], (float) args[3]);
                else
                    newPayment = new Payment(client, (Date) args[2], (float) args[3], (int) nextPaymentModifier);

                data.addPayment(client, newPayment);
                if (!loading)
                    toAdd = "rnpn " + Payment.currentPaymentNumber;

                println(String.format("Added a payment of %.2f (ID: %d) for %s %s (ID: %d)", args[3],
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
                printOrders(data.getOrdersByDate());
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
                    printPayments(data.getPaymentsByDate());
                }

                break;
            case "printcinfo": {
                Client c = (Client) args[1];
                println("ORDERS");
                printOrders(c.orders);
                println("\nPAYMENTS");
                printPayments(c.payments);
                println("\n");
                printStatement(c);
                println("\nRECEIVABLE");
                printReceivables(Arrays.asList(new Client[] {c}));
            }
            break;
            case "printt": {
                printTransactions(data.getTransactionsByDate());
            }
            break;
            case "printr": {
                printReceivables(data.getAllClients());
                println(String.format("\nTOTAL OWED: %.2f\tTOTAL PAID: %.2f\nNET: %+.2f: ", data.totalOwed, data.totalPaid, data.totalPaid-data.totalOwed));
            }
                break;
            case "prints":
                for (Client client : data.getAllClients()) {
                    printStatement(client);
                }
                break;
            case "lookupcf": {
                printClients(Levenshtein.getBestMatchesOnObject((String) args[1], data.getAllClients(), Client::getFirstName));
            }
            break;
            case "lookupcl": {
                printClients(Levenshtein.getBestMatchesOnObject((String) args[1], data.getAllClients(), Client::getLastName));
            }
            break;
            case "geto": {
                printOrders(Arrays.asList(new Order[] {(Order) args[1]}));
            }
            break;
            case "getp": {
                printPayments(Arrays.asList(new Payment[] {(Payment) args[1]}));
            }
            break;
            case "getc": {
                printClients(Arrays.asList(new Client[] {(Client) args[1]}));
            }
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
                nextOrderModifier = (int) args[1];
                break;
            case "rncn":
                nextClientModifier = (int) args[1];
                break;
            case "rnpn":
                nextPaymentModifier = (int) args[1];
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
            if (toAdd != null) {
                data.addCommand(toAdd);
                toAdd = null;
            }

        }        
    }
}