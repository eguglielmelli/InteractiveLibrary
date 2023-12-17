
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * Class represents a helper to call all the database functions that the user wants
 */
public class DatabaseService {
    private Database db;

    public DatabaseService() {
        db = new Database();
    }

    /**
     * Tries to find the user in the database
     * @param email of user
     * @param password of user
     * @return null if user not in table, else return a new user
     */
    public User getUserFromDB(String email,String password) {
        User user;
        try {
            user = db.getUser(email,password);
        }catch(SQLException e) {
            return null;
        }
        return user;
    }

    /**
     * Adds a user to the database after they sign up
     * @param name of user
     * @param email of user
     * @param password of user
     * @param contactNumber of user
     * @param registrationDate of user
     * @return true if inserted into database, otherwise false
     */
    public boolean addUserToDB(String name, String email, String password, String contactNumber, LocalDate registrationDate) {
        if (!db.addUser(name,email,password,contactNumber,registrationDate)) {
            System.out.println("User could not be added.");
            return false;
        }
        System.out.println("User successfully added to database!");
        return true;
    }

    /**
     * When user checks out a book, this function adds it to the loans_info table and decrements the available copies of the book
     * @param loan of user
     * @return true if loan was added, false otherwise
     */
    public boolean addLoanToDB(Loan loan) {
        if(!db.addLoan(loan)) {
            System.out.println("Checkout was unsuccessful. Try again at a later time.");
            return false;
        }
        db.decrementAvailableCopies(loan.getBookID());
        System.out.println("Checkout was successful! Your book can now be seen in your checked out books.");
        return true;
    }

    /**
     * This method finds the books that are applicable to the users search term
     * @param searchTerm user search term
     * @return a map with the bookID as key and book object as value
     */
    public Map<String,Book> searchBooks(String searchTerm) {
        Map<String,Book> map;
        try {
            map = db.searchBooks(searchTerm);
        }catch(SQLException e) {
            System.out.println("Error connecting to database.");
            return null;
        }
        return map;
    }

    /**
     * Shows the user books currently on loan
     * @param userID ID of user who has books checked out
     * @return a set of BookIDs
     */
    public Set<Integer> seeBooksOnLoan(int userID) {
        try {
            Set<Integer> bookIDs = db.showCurrentlyCheckedOutBooks(userID);
            if(bookIDs == null) {
                System.out.println("You have no books checked out.");
                return null;
            }
            return bookIDs;
        }catch(SQLException e) {
            System.out.println("Error finding checked out books. Try again.");
            return null;
        }
    }

    /**
     * Increments the available copies of a book after it is returned
     * @param bookID ID of book
     */
    public void incrementAvailableCopies(int bookID) {
        db.incrementAvailableCopies(bookID);
    }

    /**
     * Returns the book into the database
     * @param bookID of book to be returned
     * @param returnDate date that user returned the book
     * @param userID ID of user
     */
    public void returnBook(int bookID,LocalDate returnDate,int userID) {
        try {
            db.returnBook(bookID,returnDate,userID);

        }catch(SQLException e) {
            System.out.println("Error connecting to database. Try again");
            return;
        }
        System.out.println("Return was successful!");
    }

    /**
     * Deletes the account of user, so long as they do not have any books out on loan
     * @param userID ID of user
     * @return true if user was deleted, else false
     */
    public boolean deleteAccount(int userID) {
        try {
           if(db.deleteUser(userID)) {
                return true;
           }
        }catch(SQLException e) {
            return false;
        }
        return false;
    }

    /**
     * Shows the history of loans for the user
     * @param userID ID of user
     */
    public void seeUserLoanHistory(int userID) {
        try {
            db.seeUsersLoanHistory(userID);
            return;
        }catch(SQLException e) {
            System.out.println("Error fetching user history. Please try again.");
            return;
        }
    }
}
