
import java.time.LocalDate;
import java.util.*;

/**
 * Class represents the driver for the user where they can do all the functions
 * such as check out, search, and return books
 * Holds a database service to make the calls to the database
 */
public class UserService {

    private User user;
    private DatabaseService dbs;
    Scanner scanner;


    public UserService(User user,Scanner scanner) {
        this.user = user;
        dbs = new DatabaseService();
        this.scanner = scanner;
    }

    /**
     * Represents a user menu once they have logged in, this is where they choose from the list of options
     * @param user
     * @return true if the user has decided they want to log out
     */
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
                return true;
            } else if(choice.equals("5")) {
                    if(deleteAccount()) {
                        return true;
                    }
            }else {
                System.out.println("That is not a valid choice. Please choose again.");
            }
        }
    }

    /**
     * Method for users to search books, it uses fuzzy search so relevant books will also come up in their
     * search, and they can check them out
     */
    public void searchBooks() {
        System.out.println("Enter your search term, you can search by author,genre, or title: ");
        System.out.print("> " );

        String searchTerm = scanner.nextLine();
        Map<String,Book> bookMap = dbs.searchBooks(searchTerm);

        if(!bookMap.isEmpty()) {
            System.out.println("Select an ID of the book you would like to check out, or press q to return to the options menu:");
            System.out.print("> ");
            String bookChoice = scanner.nextLine();
            if(bookChoice.equals("q")) {
                return;
            }

            while(!bookMap.containsKey(bookChoice)) {
                System.out.println("That is not a valid ID. Please choose from the list, or press q to return to the options menu:");
                System.out.print("> ");
                bookChoice = scanner.nextLine();
                if(bookChoice.equals("q")) {
                    return;
                }
            }
            checkOutBook(bookChoice);
        }
        else {
            System.out.println();
            System.out.println("There are no books that fit that search criteria. Try again.");
            return;
        }
        return;
    }

    /**
     * Method uses the database service attribute to write back to the database after user decides what
     * book to check out
     * @param bookID to be checked out
     */
    public void checkOutBook(String bookID) {
            dbs.addLoanToDB(Integer.parseInt(bookID),user.getUserID(),LocalDate.now(),LocalDate.now().plusDays(30),null);
            return;
    }

    /**
     * This method allows the user to enter their information and be added to the database, takes in name, email, password, phone number
     * and then writes in the day they registered their account to the database
     */
    public void addUser() {
        System.out.println("Please enter your name: ");
        System.out.print("> ");
        String name = scanner.nextLine().strip().toLowerCase(Locale.ROOT);

        System.out.println("Please enter your email: ");
        System.out.print("> ");
        String email = scanner.nextLine().strip().toLowerCase(Locale.ROOT);

        System.out.println("Please enter your password: " );
        System.out.print("> ");
        String password = scanner.nextLine().strip();

        System.out.println("Please enter your contact number with format xxx-xxx-xxxx");
        System.out.print("> ");
        String contactNumber = scanner.nextLine().strip();

        LocalDate registrationDate = LocalDate.now();
        dbs.addUserToDB(name,email,password,contactNumber,registrationDate);
    }

    /**
     * Allows user to see the books they have out on loan
     * @return set with book IDs that are on loan
     */
    public Set<Integer> seeBooksOnLoan() {
        return dbs.seeBooksOnLoan(user.getUserID());
    }

    /**
     * method for user to return the book, they do so by choosing a valid bookID from the set returned by seeBooksOnLoan()
     */
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

    /**
     * checks if the input is a valid int
     * @param input string that will be parsed to int
     * @return true/false
     */
    public boolean validateInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Please enter an integer.");
            return false;
        }
    }

    /**
     * Method for user to delete their account, they cannot do so if they have books on loan
     * @return true/false
     */
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
