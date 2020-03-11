package app;

import java.util.ArrayList;
import java.util.HashMap;

class DataManager {
    ArrayList<String> commandLog = new ArrayList<String>();

    ArrayList<Order> orders = new ArrayList<Order>();
    ArrayList<Client> clients = new ArrayList<Client>();
    ArrayList<Payment> payments = new ArrayList<Payment>();

    HashMap<Integer, Client> clientLookup = new HashMap<Integer, Client>();
    HashMap<Integer, Order> orderLookup = new HashMap<Integer, Order>();

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
        orders.add(order);
    }

    void addClient(Client client) {
        clients.add(client);
    }

    void addPayment(Payment payment) {
        payments.add(payment);
    }

    Order getOrder(int orderId) {
        return orderLookup.get(orderId);
    }

    Order getClient(int clientId) {
        return orderLookup.get(clientId);
    }
}