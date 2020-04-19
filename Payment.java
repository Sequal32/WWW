package app;

import java.util.Date;

public class Payment extends Transaction {
    static int currentPaymentNumber = 0;

    double amount;
    int paymentNumber;

    public Payment(Client client, Date paymentDate, double amount) {
        this.paymentNumber = ++currentPaymentNumber;
        this.client = client;
        this.amount = amount;
        this.date = paymentDate;
    }

    public Date getDate() {
        return date;
    }
}