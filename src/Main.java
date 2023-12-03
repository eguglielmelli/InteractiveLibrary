import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws SQLException {

        Database db = new Database();

        UserService us = new UserService();
        Display ds = new Display();
        //db.clearLoanTable();

        DatabaseService dbs = new DatabaseService();

        db.searchBooks("george ");

        //ds.displayMenu();
        //us.logIn();

    }
}
