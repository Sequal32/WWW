package app;

import java.util.Date;

public class Transaction {
    double transactionAmount;
    Date date;
    Client client;

    Date getDate() {return date;}
    double getAmount() {return transactionAmount;}
}