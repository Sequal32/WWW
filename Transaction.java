package app;

import java.util.Date;

public class Transaction {
    int ID;
    float transactionAmount;
    Date date;
    Client client;

    Date getDate() {return date;}
    float getAmount() {return transactionAmount;}
    void changeId(int newId) {ID = newId;}
}