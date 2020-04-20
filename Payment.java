package app;

import java.util.Date;

public class Payment extends Transaction {
    static int currentPaymentNumber;

    public Payment(Client client, Date paymentDate, double amount) {
        this.ID = ++currentPaymentNumber;
        this.client = client;
        this.transactionAmount = amount;
        this.date = paymentDate;
    }

    public Date getDate() {
        return date;
    }
}