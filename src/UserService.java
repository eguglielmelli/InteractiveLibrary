import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class UserService {

    private Database db = new Database();
    private String email = null;
    private String password = null;
    private User user;
    private DatabaseService dbs;
    Scanner scanner;


    public UserService(User user) {
        this.user = user;
        dbs = new DatabaseService();
        scanner = new Scanner(System.in);
    }
    public void userMenu(User user) {
        while (true) {
            System.out.println("Welcome " + user.getName() + "! Please choose from these search options: " +
                    "\n 1. Search Books" +
                    "\n 2. See Checked Out Books" +
                    "\n 3. Return Book" +
                    "\n 4. Log Out");

            System.out.print("> ");
            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                searchBooks();
                System.out.println();
            } else if (choice.equals("2")) {
                System.out.println();
                seeBooksOnLoan();
                System.out.println();
            } else if (choice.equals("3")) {
                System.out.println();
                returnBook();
                System.out.println();
            } else if(choice.equals("4")) {
                System.out.println("Thank you for using the library. Goodbye!");
                break;
            }else {
                System.out.println("That is not a valid choice. Please choose again.");
            }
        }
    }
    public void searchBooks() {
        System.out.println("Enter your search term, you can search by author,genre, or title: ");
        System.out.print("> " );
        String searchTerm = scanner.nextLine();
        Map<String,Book> bookMap = dbs.searchBooks(searchTerm);
        if(!bookMap.isEmpty()) {
            System.out.println("Select an ID of the book you would like to check out, or press q to return to the options menu");
            System.out.print("> ");
            String bookChoice = scanner.nextLine();
            if(bookChoice.equals("q")) {
                userMenu(user);
                return;
            }
            while(!bookMap.containsKey(bookChoice)) {
                System.out.println("That is not a valid ID. Please choose from the list.");
                System.out.print("> ");
                bookChoice = scanner.nextLine();
                if(bookChoice.equals("q")) {
                    userMenu(user);
                }
            }
            checkOutBook(bookChoice);
        }
        return;
    }

    public void checkOutBook(String bookID) {
            dbs.addLoanToDB(Integer.parseInt(bookID),user.getUserID(),LocalDate.now(),LocalDate.now().plusDays(30),null);
            return;
    }

    public void addUser() {
        System.out.println("Please enter your name: ");
        System.out.print("> ");
        String name = scanner.nextLine().toLowerCase(Locale.ROOT);
        System.out.println("Please enter your email: ");
        System.out.print("> ");
        String email = scanner.nextLine().toLowerCase(Locale.ROOT);
        System.out.println("Please enter your password: " );
        System.out.print("> ");
        String password = scanner.nextLine();
        System.out.println("Please enter your contact number with format xxx-xxx-xxxx");
        System.out.print("> ");
        String contactNumber = scanner.nextLine();
        LocalDate registrationDate = LocalDate.now();
        db.addUser(name,email,password,contactNumber,registrationDate);

    }
    public void seeBooksOnLoan() {
        dbs.seeBooksOnLoan(user.getUserID());
    }

    public void returnBook() {
        System.out.println("If you know the book ID that you would like to return, feel free to enter it. If you would like to see your currently checked out books," +
                "press 'y'.");
        System.out.print("> " );
        String choice = scanner.nextLine();
        if(choice.equals("y")) {
            seeBooksOnLoan();
        }
        System.out.println("Input book ID:");
        System.out.print("> " );
        int bookID = Integer.parseInt(scanner.nextLine());
        dbs.returnBook(bookID,LocalDate.now(),user.getUserID());
        dbs.incrementAvailableCopies(bookID);
    }
}
