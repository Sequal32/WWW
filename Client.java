package app;
public class Client{
    private static int currentClientNumber;

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
}