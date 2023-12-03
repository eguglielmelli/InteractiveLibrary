import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.time.LocalDate;

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
        System.out.println("Checkout was successful! Your book can now be seen in your checked out books.");
        return true;
    }
    public void getBookByTitle(String title) {
        try {
            db.getBooksByTitle(title);
        }catch(SQLException e) {
            System.out.println("Error getting title. Try again.");
            return;
        }
    }

    public void getBookByAuthor(String author) {
        try {
            db.getBooksByAuthor(author);
        }catch(SQLException e) {
            return;
        }
    }
}
