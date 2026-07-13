package base.accountsHandler;

import java.io.Serializable;

public class User implements Serializable {

    // Identificador
    private static final long serialVersionUID = 1L;

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
        Data.saveData();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        Data.saveData();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        Data.saveData();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        Data.saveData();
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