package app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

class DataManager {
    ArrayList<String> commandLog = new ArrayList<String>();

    HashMap<Integer, Client> clients;
    HashMap<Integer, Order> orders;
    HashMap<Integer, Payment> payments;

    // String sizes for support fit - all to initially fit headings
    int clientNumberSize = 3;
    int clientNameSize = 5;

    int orderNumberSize = 3;
    int orderAmountSize = 5;
    
    int brandSize = 5;
    int tierSize = 4;
    int repairPriceSize = 4;
    
    int paymentNumberSize = 5;
    int paymentAmountSize = 5;
    // Totals
    float totalPaid = 0;
    float totalOwed = 0;

    String currentStore;

    DataManager() {
        clearData();
    }

    void clearData() {
        clients = new HashMap<Integer, Client>();
        orders = new HashMap<Integer, Order>();
        payments = new HashMap<Integer, Payment>();
    }

    private String prepareName(String s) {
        return s.trim().concat(".txt");
    }

    private boolean storeExists(String s) {
        return Support.fileExists(prepareName(s));
    }

    String[] fetchStore(String s) {
        return Support.readTextFile(prepareName(s)).split("\n");
    }
    
    // Returns success
    boolean loadStore(String s, boolean overwrite) {
        if (!storeExists(s)) return false;
        if (overwrite) clearData();

        commandLog = new ArrayList<String>();
        currentStore = s;
        for (String cmd : fetchStore(s)) {
            addCommand(cmd);
        }
        saveLastStore(s);

        return true;
    }

    void loadDefaultStore() {
        
    }

    void startup() {
        if (Support.fileExists("laststore.txt")) {
            // Load the last store
            if (loadStore(Support.readTextFile("laststore.txt"), true)) return;
        }
        // Load the default store
        loadStore("default", true);
    }

    void saveLastStore(String s) {
        Support.writeTextFile("laststore.txt", s);
    }

    void saveStore(String s) {
        Support.writeTextFile(prepareName(s), String.join("\n", commandLog));
        saveLastStore(s);
    }

    void addCommand(String s) {
        commandLog.add(s);
    }

    int getDigits(int x) {
        return Math.max((int) (Math.log10(x) + 1), 2);
    }

    // Manage the data
    void addOrder(Client client, Order order) {
        client.addOrder(order);
        orders.put(order.ID, order);
        orderNumberSize = Math.max(orderNumberSize, getDigits(order.ID));
        orderAmountSize = Math.max(orderAmountSize, getDigits((int) order.transactionAmount) + 3);
        totalOwed += order.transactionAmount;
    }

    void addClient(Client client) {
        clients.put(client.clientNumber, client);
        clientNumberSize = Math.max(clientNumberSize, getDigits(client.clientNumber));
        clientNameSize = Math.max(clientNameSize, client.fullName.length());
    }

    void addPayment(Client client, Payment payment) {
        client.addPayment(payment);
        payments.put(payment.ID, payment);
        paymentNumberSize = Math.max(paymentNumberSize, getDigits(payment.ID));
        paymentAmountSize = Math.max(paymentAmountSize, getDigits((int) payment.transactionAmount) + 3);
        totalPaid += payment.transactionAmount;
    }

    void addRepairPrice(RepairPrice rp) {
        Prices.addRepairPrice(rp);
        brandSize = Math.max(brandSize, rp.brand.length());
        tierSize = Math.max(tierSize, rp.tier.length());
        repairPriceSize = Math.max(repairPriceSize, getDigits((int) rp.price) + 3);
    }

    Order getOrder(int orderId) {
        return orders.get(orderId);
    }

    Collection<Client> getClientsById() {
        List<Client> clientsSorted = new ArrayList<Client>(clients.values());
        Collections.sort(clientsSorted, Comparator.comparing(Client::getId));
        return clientsSorted;
    }

    Collection<Transaction> getTransactionsByDate() {
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.addAll(orders.values());
        transactions.addAll(payments.values());
        Collections.sort(transactions, Comparator.comparing(Transaction::getDate));
        return transactions;
    }

    Collection<Client> getClientsByName() {
        List<Client> clientsSorted = new ArrayList<Client>(clients.values());
        Collections.sort(clientsSorted, Comparator.comparing(Client::getName));
        return clientsSorted;
    }

    Collection<Payment> getPaymentsByDate() {
        List<Payment> paymentsSorted = new ArrayList<Payment>(payments.values());
        Collections.sort(paymentsSorted, Comparator.comparing(Payment::getDate));
        return paymentsSorted;
    }

    Collection<Order> getOrdersByDate() {
        List<Order> ordersSorted = new ArrayList<Order>(orders.values());
        Collections.sort(ordersSorted, Comparator.comparing(Order::getDate));
        Collections.reverse(ordersSorted);
        return ordersSorted;
    }

    Client getClient(int clientId) {
        return clients.get(clientId);
    }

    Payment getPayment(int paymentId) {
        return payments.get(paymentId);
    }

    Collection<Order> getAllOrders() {
        return orders.values();
    }

    Collection<Client> getAllClients() {
        return clients.values();
    }

    Collection<Payment> getAllPayments() {
        return payments.values();
    }
}