import accountsHandler.Data;
import accountsHandler.Login;
import accountsHandler.Register;
import accountsHandler.User;
import mainProgram.Home;

import java.util.Scanner;

public class Main {

    public static void main() {

        Scanner keyboard = new Scanner(System.in);

        int choice = 0, exit = 0;

        while (exit == 0){
            System.out.println("choice: 1. login 2. register 3. display 4. exit");
            choice = keyboard.nextInt();
            keyboard.nextLine();

            switch (choice){
                case 1:
                    User loginTry = Login.execLogin();
                    if(loginTry != null){
                        Home.execHome(loginTry);
                    }
                    break;
                case 2:
                    Register.execRegister();
                    break;
                case 3:
                    for (User user : Data.retrieveAllUsers()){
                        System.out.println(user.toString());
                    }
                    break;
                case 4:
                    exit = 1;
            }
        }

    }

}
