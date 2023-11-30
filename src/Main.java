import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException {

        Database db = new Database();

        UserService us = new UserService();
        Display ds = new Display();
        //db.clearLoanTable();
        //db.decrementAvailableCopies(1);
       // db.decrementAvailableCopies(2);
        us.logIn();
        db.printLoansTable();
        //ds.displayMenu();
        //us.logIn();

    }
}
