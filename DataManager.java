package app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

class DataManager {
    ArrayList<String> commandLog = new ArrayList<String>();

    HashMap<Integer, Client> clients = new HashMap<Integer, Client>();
    HashMap<Integer, Order> orders = new HashMap<Integer, Order>();
    HashMap<Integer, Payment> payments = new HashMap<Integer, Payment>();


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
        for (String cmd : fetchStore(s)) {
            commandLog.add(cmd);
        }
        saveLastStore(s);

        return true;
    }

    void loadDefaultStore() {
        loadStore("default", true);
    }
    
    void loadLastStore() {
        loadStore(Support.readTextFile("laststore.txt"), true);
    }

    void startup() {
        if (Support.fileExists("laststore.txt"))
            loadLastStore();
        else
            loadDefaultStore();
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

    // Manage the data
    void addOrder(Order order) {
        orders.put(order.orderNumber, order);
    }

    void addClient(Client client) {
        clients.put(client.clientNumber, client);
    }

    void addPayment(Payment payment) {
        payments.put(payment.paymentNumber, payment);
    }

    Order getOrder(int orderId) {
        return orders.get(orderId);
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