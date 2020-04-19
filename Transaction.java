package app;

import java.util.Date;

public class Transaction {
    int ID;
    double transactionAmount;
    Date date;
    Client client;

    Date getDate() {return date;}
    double getAmount() {return transactionAmount;}
    void changeId(int newId) {ID = newId;}
}