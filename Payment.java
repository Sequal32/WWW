package app;

public class Payment{
  Client client;
  double amount;
  long paymentDate;

  public Payment(Client client, double amount, long paymentDate){
        this.client = client;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }
}