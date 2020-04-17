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

    // Analysis reports
    private void printStatement(Client c) {
        StringBuilder desc = new StringBuilder();
        desc.append(String.format("STATEMENT FOR %s\n", c.fullName));

        for (Order order : c.orders) {
            desc.append(String.format("+%.2f\t%s TIER %s TUNEUP\n", order.transactionAmount, order.tier, order.brand));
        }
        for (Payment payment : c.payments) {
            desc.append(String.format("-%.2f\tPAID\n", payment.amount));
        }
        desc.append(String.format("TOTAL: %+.2f", c.outstandingAmount));
        println(desc.toString());
    }

    private void printTransactions() {
        StringBuilder desc = new StringBuilder();
        println(String.format("%s\t%10s\t%10s\t%s", Support.fit("CID", data.clientNumberSize), "TYPE", "DATE", Support.fit("AMOUNT", data.paymentAmountSize)));

        for (Transaction transaction : data.getTransactionsByDate()) {
            String type = transaction instanceof Order ? "ORDER" : "PAYMENT";
            println(String.format("%s\t%10s\t%10s\t%s", Support.fit(String.valueOf(transaction.client.clientNumber), data.clientNumberSize), type, Support.dateToString(transaction.date), Support.fit(String.valueOf(transaction.transactionAmount), data.paymentAmountSize)));
        }

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
            case "addp": {
                Client client = (Client) args[1];
                Payment newPayment = new Payment(client, (Double) args[2], (Date) args[3]);
                data.addPayment(newPayment);
                println(String.format("Added a $%.2f payment (ID: %d) for %s %s (ID: %d)", args[2],
                        newPayment.paymentNumber, client.firstName, client.lastName, client.clientNumber));
            }
                break;
            case "comp":
                Order order = (Order) args[1];
                order.markComplete();
                println(String.format("%s's order for a %s tuneup on type %s (ID: %d) marked complete on %s.",
                        order.client.firstName, order.tier, order.brand, order.orderNumber,
                        printingFormat.format(order.completionDate)));
                break;
            case "printo": {
                if (data.orders.size() == 0) {println("No orders found."); break;}

                Collection<Order> orders = data.getAllOrders();

                println(String.format("%s\t%s\t%s\t%s\tClient ID\tprice", Support.fit("brands", data.brandSize), Support.fit("tier", data.tierSize), Support.fit("Client Name", data.clientNameSize)));

                int count = 0;
                int pageCount = calculatePageCount(orders.size());
                for (Order o : orders) {
                    println(String.format("%s\t%s\t%s\t%.2f", Support.fit(o.brand, data.brandSize), Support.fit(o.tier, data.brandSize), Support.fit(o.client.fullName, data.tierSize), o.repairPrice));
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

                println(String.format("%s\t%s\tprice", Support.fit("brands", data.brandSize), Support.fit("tier", data.tierSize)));

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

                    println(String.format("%s\t%s\t%s\tprice", Support.fit("id", data.paymentNumberSize), Support.fit("cid", data.clientNumberSize), Support.fit("price", data.paymentAmountSize)));

                    int count = 1;
                    int pageCount = calculatePageCount(data.payments.size());
                    for (Payment p : data.getAllPayments()) {
                        count++;
                        println(String.format("%s\t%s\t%s\t%s", Support.fit(String.valueOf(p.paymentNumber), data.paymentNumberSize), Support.fit(String.valueOf(p.client.clientNumber), data.clientNumberSize), Support.fit(String.format("%.2f", p.amount), data.paymentAmountSize), Support.dateToString(p.date))));
                        if (count % PAGE_SIZE == 0) if (!promptNextPage(count, pageCount)) break;                  
                    }
                }

                break;
            case "printt":
                break;
            case "printr": {
                if (data.payments.size() == 0) {println("No clients found."); break;}

                println(String.format("%s\t%s\t%s\tprice", Support.fit("cid", data.clientNumberSize), Support.fit("name", data.clientNameSize), Support.fit("amount owed", data.paymentAmountSize)));

                int count = 1;

                int pageCount = calculatePageCount(data.clients.size());
                for (Client client : data.getAllClients()) {
                    count++;
                    println(String.format("%s\t%s\t%s\tprice", Support.fit(String.valueOf(client.clientNumber), data.clientNumberSize), Support.fit(client.fullName, data.clientNameSize), Support.fit(client.outstandingAmount, data.paymentAmountSize)));
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