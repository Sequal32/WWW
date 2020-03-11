package app;

public class Payment{
    private static int currentPaymentNumber = 0;

    Client client;
    double amount;
    long paymentDate;
    int paymentNumber;

    public Payment(Client client, double amount, long paymentDate) {
        this.paymentNumber = ++currentPaymentNumber;
        this.client = client;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }
}