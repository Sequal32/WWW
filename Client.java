package app;

import java.util.ArrayList;

public class Client {
    static int currentClientNumber = 0;

    int clientNumber;
    double outstandingAmount = 0;
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

    void addOrder(Order o) {
        outstandingAmount += o.transactionAmount;
        orders.add(o);
    }

    void addPayment(Payment p) {
        outstandingAmount -= p.amount;
        payments.add(p);
    }

    public int getId() {
        return clientNumber;
    }

    public String getName() {
        return fullName;
    }
}