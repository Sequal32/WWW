package app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

class DataManager {
    ArrayList<String> commandLog = new ArrayList<String>();

    HashMap<Integer, Client> clients = new HashMap<Integer, Client>();
    HashMap<Integer, Order> orders = new HashMap<Integer, Order>();
    HashMap<Integer, Payment> payments = new HashMap<Integer, Payment>();

    String[] fetchStore(String s) {
        return Support.readTextFile(s.trim().concat(".txt")).split("\n");
    }

    void loadStore(String s) {
        commandLog = new ArrayList<String>();
        for (String cmd : fetchStore(s)) {
            commandLog.add(cmd);
        }
        saveLastStore(s);
    }

    void loadDefaultStore() {
        loadStore("default");
    }
    
    void loadLastStore() {
        loadStore(Support.readTextFile("laststore.txt"));
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
        Support.writeTextFile(s.concat(".txt"), String.join("\n", commandLog));
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