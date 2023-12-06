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


    public UserService(User user,Scanner scanner) {
        this.user = user;
        dbs = new DatabaseService();
        this.scanner = scanner;
    }
    public boolean userMenu(User user) {
        while (true) {
            System.out.println("Welcome " + user.getName() + "! Please choose from these search options: " +
                    "\n 1. Search Books" +
                    "\n 2. See Checked Out Books" +
                    "\n 3. Return Book" +
                    "\n 4. Log out" +
                    "\n 5. Delete Account");

            System.out.print("> ");
            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                searchBooks();
                System.out.println();
            } else if (choice.equals("2")) {
                seeBooksOnLoan();
                System.out.println();
            } else if (choice.equals("3")) {
                returnBook();
                System.out.println();
            } else if (choice.equals("4")) {
                // Do not close the scanner here, just break from the loop
                return true;  // Indicate that the user chose to log out
            } else if(choice.equals("5")) {
                    if(deleteAccount()) {
                        return true;
                    }
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
        dbs.addUserToDB(name,email,password,contactNumber,registrationDate);
    }

    public Set<Integer> seeBooksOnLoan() {
        return dbs.seeBooksOnLoan(user.getUserID());
    }

    public void returnBook() {
        Set<Integer> bookIDs = seeBooksOnLoan();
        if(bookIDs == null){
            return;
        }
        System.out.println("Please enter the ID of the book you would like to return:");
        System.out.print("> " );
        String choice = scanner.nextLine();
        while(!validateInteger(choice)) {
            System.out.print("> ");
            choice = scanner.nextLine();
        }
        int bookToBeReturned = Integer.parseInt(choice);
        if(!bookIDs.contains(bookToBeReturned)) {
            System.out.println("That ID does not correspond to your current checked out books.");
            return;
        }
        dbs.returnBook(bookToBeReturned,LocalDate.now(),user.getUserID());
        dbs.incrementAvailableCopies(bookToBeReturned);
    }

    public boolean validateInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Please enter an integer.");
            return false;
        }
    }
    public boolean deleteAccount() {
        System.out.println("Are you sure you would like to delete your account? If yes, enter 'y':");
        System.out.print("> ");
        String choice = scanner.nextLine();
        if(choice.equals("y")) {
            if(dbs.deleteAccount(user.getUserID())) {
                return true;
            }
        }
        return false;
    }
}
