
import java.util.Scanner;

public class Display {

    private UserService userService;
    private DatabaseService dbs;

    public Display() {
        this.userService = null;
        this.dbs = new DatabaseService();
    }
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to the Library! Please choose from these two options: " +
                    "\n1. Log in" +
                    "\n2. Sign up" +
                    "\n3. Exit system");

            System.out.print("> ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                User user = logIn();
                if (user != null) {
                    userService = new UserService(user, scanner);  // Pass scanner to UserService
                    boolean isLoggedOut = userService.userMenu(user);
                    if (isLoggedOut) {
                        continue;  // Go back to the main menu
                    }
                } else {
                    System.out.println("There is no user with those credentials.");
                }
            } else if (choice.equals("2")) {
                userService = new UserService(null, scanner);  // Pass scanner to UserService
                userService.addUser();
            } else if (choice.equals("3")) {
                System.out.println("Thank you for using the library. Goodbye!");
                break;  // Exit the loop and the application
            } else {
                System.out.println("Please choose between 1,2, and 3.");
            }
        }

        scanner.close();  // Close the scanner when completely done with it
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
