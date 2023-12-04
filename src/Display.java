import java.sql.SQLException;
import java.util.Scanner;

public class Display {

    private UserService userService;
    private DatabaseService dbs;

    public Display() {
        this.userService = null;
        this.dbs = new DatabaseService();



    }
    public void displayMenu() {
        System.out.println("Welcome to the Library! Please choose from these two options: " +
                "\n1. Log in" +
                "\n2. Exit system");

        Scanner scanner = new Scanner(System.in);
        System.out.print("> ");
        String choice = scanner.nextLine();

        while(!choice.equals("1") && !choice.equals("2")) {
            System.out.println("Please choose between 1 and 2.");
            System.out.print("> ");
            choice = scanner.nextLine();
        }
        if(choice.equals("1")) {
            User user = logIn();
            if(user != null) {
                userService = new UserService(user);
                userService.userMenu(user);
            }else {
                System.out.println("There is no user with those credentials. Press ");
            }
        }else {
            System.out.println("Thank you for using the library. Goodbye!");
            return;
        }
    }
    public User logIn() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your email:");
        System.out.print("> ");
        String email = scanner.nextLine();
        System.out.println("Please enter your password:");
        System.out.print("> ");
        String password = scanner.nextLine();
        User user = dbs.getUserFromDB(email,password);
        if(user != null) {
            return user;
        }
        return null;
    }

}
