
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class DatabaseService {
    private Database db;

    public DatabaseService() {
        db = new Database();
    }

    public User getUserFromDB(String email,String password) {
        User user;
        try {
            user = db.getUser(email,password);
        }catch(SQLException e) {
            return null;
        }
        return user;
    }

    public boolean addUserToDB(String name, String email, String password, String contactNumber, LocalDate registrationDate) {
        if (!db.addUser(name,email,password,contactNumber,registrationDate)) {
            System.out.println("User could not be added.");
            return false;
        }
        System.out.println("User successfully added to database!");
        return true;
    }

    public boolean addLoanToDB(int bookID, int userID, LocalDate checkOutDate, LocalDate dueDate, LocalDate returnDate) {
        if(!db.addLoan(bookID,userID,checkOutDate,dueDate,null)) {
            System.out.println("Checkout was unsuccessful. Try again at a later time.");
            return false;
        }
        db.decrementAvailableCopies(bookID);
        System.out.println("Checkout was successful! Your book can now be seen in your checked out books.");
        return true;
    }

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

    public void seeBooksOnLoan(int userID) {
        try {
            db.showCurrentlyCheckedOutBooks(userID);
        }catch(SQLException e) {
            System.out.println("Error finding checked out books. Try again.");
            return;
        }
    }
    public void incrementAvailableCopies(int bookID) {
        db.incrementAvailableCopies(bookID);
    }
    public void returnBook(int bookID,LocalDate returnDate,int userID) {
        try {
            db.returnBook(bookID,returnDate,userID);

        }catch(SQLException e) {
            System.out.println("Error connecting to database. Try again");
            return;
        }
        System.out.println("Return was successful!");
    }
}
