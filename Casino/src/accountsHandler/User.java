package accountsHandler;

public class User {

    private String name;
    private String email;
    private String password;
    private double balance;
    
    public static int numberOfUsers;

    public User(String name, String email, String password, double balance) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.balance = balance;
        numberOfUsers++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString(){
        return "Usuario: " +
                "\n\tname = "+name+
                "\n\temail = "+email+
                "\n\tpassword = "+password+
                "\n\tbalance = "+balance;
    }
}
