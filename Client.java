package app;
public class Client{
    static int currentClientNumber = 0;

    int clientNumber;
    String firstName;
    String lastName;
    String fullName;

    public Client(String firstName, String lastName){
        this.clientNumber = ++currentClientNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = this.firstName + " " + this.lastName;
    }

    public int getId() {
        return clientNumber;
    }

    public String getName() {
        return fullName;
    }
}