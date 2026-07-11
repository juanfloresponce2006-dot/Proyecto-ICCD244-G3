package base;

import base.accountsHandler.Data;
import base.accountsHandler.LoginConsole;
import base.accountsHandler.RegisterConsole;
import base.accountsHandler.User;
import base.mainProgram.HomeConsole;

import java.util.Scanner;

public class MainConsole {

    public static void main() {

        Scanner keyboard = new Scanner(System.in);

        int choice = 0, exit = 0;

        while (exit == 0){
            System.out.println("choice: 1. login 2. register 3. display 4. exit");
            choice = keyboard.nextInt();
            keyboard.nextLine();

            switch (choice){
                case 1:
                    LoginConsole loginInstance = new LoginConsole();
                    
                    //User loginTry = LoginConsole.execLogin();
                    User loginTry = loginInstance.execLogin();
                    
                    if(loginTry != null){
                        HomeConsole.execHome(loginTry);
                    }
                    break;
                case 2:
                    RegisterConsole.execRegister();
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
