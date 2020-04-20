package app;

import java.util.ArrayList;

public class Client {
    static int currentClientNumber = 0;

    int clientNumber;
    float outstandingAmount = 0;
    float totalPaid = 0;
    String firstName;
    String lastName;
    String fullName;
    
    ArrayList<Order> orders = new ArrayList<Order>();
    ArrayList<Payment> payments = new ArrayList<Payment>();

    public Client(String firstName, String lastName){
        this.clientNumber = ++currentClientNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = Support.capitalizeFirstLetter(firstName) + " " + Support.capitalizeFirstLetter(lastName);
    }

    public Client(String firstName, String lastName, int clientNumber){
        this(firstName, lastName);
        this.clientNumber = clientNumber;
        currentClientNumber = Math.max(currentClientNumber, clientNumber);
    }

    void addOrder(Order o) {
        outstandingAmount += o.transactionAmount;
        orders.add(o);
    }

    void addPayment(Payment p) {
        outstandingAmount -= p.transactionAmount;
        totalPaid += p.transactionAmount;
        payments.add(p);
    }

    public int getId() {
        return clientNumber;
    }

    public String getName() {
        return fullName;
    }
    
    public Payment getLastPayment() {
        if (payments.size() == 0)
            return null;
        return payments.get(payments.size() - 1);
    }
}