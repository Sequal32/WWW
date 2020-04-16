package app;

import java.util.Date;

public class Payment {
    static int currentPaymentNumber = 0;

    Client client;
    double amount;
    Date paymentDate;
    int paymentNumber;

    public Payment(Client client, double amount, Date paymentDate) {
        this.paymentNumber = ++currentPaymentNumber;
        this.client = client;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public Date getDate() {
        return paymentDate;
    }
}