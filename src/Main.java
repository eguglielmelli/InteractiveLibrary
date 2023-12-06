import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        Database db = new Database();
        Display ds = new Display();
        ds.displayMenu();
    }
}
