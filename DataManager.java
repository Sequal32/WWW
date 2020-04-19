package app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

class DataManager {
    ArrayList<String> commandLog = new ArrayList<String>();

    HashMap<Integer, Client> clients;
    HashMap<Integer, Order> orders;
    HashMap<Integer, Payment> payments;
    TreeSet<Transaction> transactions;

    // String sizes for support fit - all to 8 initially to fit headings
    int clientNumberSize = 3;
    int clientNameSize = 5;

    int orderNumberSize = 3;
    
    int brandSize = 5;
    int tierSize = 5;
    int repairPriceSize = 5;
    
    int paymentNumberSize = 5;
    int paymentAmountSize = 5;

    DataManager() {
        clearData();
    }

    void clearData() {
        clients = new HashMap<Integer, Client>();
        orders = new HashMap<Integer, Order>();
        payments = new HashMap<Integer, Payment>();
        transactions = new TreeSet<Transaction>();
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
    void addOrder(Order order) {
        orders.put(order.orderNumber, order);
        orderNumberSize = Math.max(orderNumberSize, order.orderNumber);
    }

    void addClient(Client client) {
        clients.put(client.clientNumber, client);
        clientNumberSize = Math.max(clientNumberSize, getDigits(client.clientNumber));
        clientNameSize = Math.max(clientNameSize, client.fullName.length());
    }

    void addPayment(Payment payment) {
        payments.put(payment.paymentNumber, payment);
        paymentNumberSize = Math.max(paymentNumberSize, getDigits(payment.paymentNumber));
        paymentAmountSize = Math.max(paymentAmountSize, getDigits((int) payment.amount));
    }

    void addRepairPrice(RepairPrice rp) {
        Prices.addRepairPrice(rp);
        brandSize = Math.max(brandSize, rp.brand.length());
        tierSize = Math.max(tierSize, rp.tier.length());
        repairPriceSize = Math.max(repairPriceSize, getDigits((int) rp.price));
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