import java.sql.SQLException;
import java.util.Scanner;

public class Display {

    private UserService userService;

    public void displayMenu() throws SQLException {
        System.out.println("Welcome to the Library! Please choose from these two options: " +
                "\n1. Log in" +
                "\n2. Exit system");

        Scanner scanner = new Scanner(System.in);

        String choice = scanner.nextLine();

        while(!choice.equals("1") && !choice.equals("2")) {
            System.out.println("Please choose between 1 and 2.");
            choice = scanner.nextLine();
        }
        if(choice.equals("1")) {
            userService.logIn();

        }

    }

}
