import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserService {

    private Database db = new Database();

    public void seeAvailableBooks() {
        db.printBooksTables();
    }
    public void getBooksByAuthor() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the author you would like to search by: ");
        String author = scanner.nextLine();
        System.out.println(author);
        db.getBooksByAuthor(author);
        return;
    }
    public void getBooksByTitle() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the title you would like to search by: ");
        String title = scanner.nextLine();
        db.getBooksByTitle(title);
        return;

    }

    public void getBooksByGenre() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the genre you would like to search by: ");
        String genre = scanner.nextLine();
        Map<Integer,Book> map = db.getBooksByGenre(genre);
        System.out.println(map);
        return;
    }

    public Loan checkOutBook() {
        return null;
    }
    





}
