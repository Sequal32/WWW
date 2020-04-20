package app;

import java.util.Date;

public class Payment extends Transaction {
    static int currentPaymentNumber;

    public Payment(Client client, Date paymentDate, float amount) {
        this.ID = ++currentPaymentNumber;
        this.client = client;
        this.transactionAmount = amount;
        this.date = paymentDate;
    }

    public Payment(Client client, Date paymentDate, float amount, int id) {
        this(client, paymentDate, amount);
        this.ID = id;
    }

    public Date getDate() {
        return date;
    }
}