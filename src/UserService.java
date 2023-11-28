import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class UserService {

    private Database db = new Database();

    public void seeAvailableBooks() {
        db.printBooksTables();
    }

    public void userMenu(User user) throws SQLException {
        System.out.println("Welcome " + user.getName() + "! Please choose from these search options " +
                "\n 1. Search by Author " +
                "\n 2. Search by Genre " +
                "\n 3. Search by Title " +
                "\n 4. Log out");

        Scanner scanner = new Scanner(System.in);
        System.out.print("> ");
        String choice = scanner.nextLine();
        while(!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4")) {
            System.out.println("That is a not a valid choice. Please choose again.");
            System.out.print("> ");
            choice = scanner.nextLine();
        }
        switch(choice) {
            case "1":
                getBooksByAuthor();
                break;
            case "2":
                getBooksByGenre();
                break;
            case "3":
                getBooksByTitle();
                break;
        }


    }

    public void getBooksByAuthor() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the author you would like to search by: ");
        System.out.print("> ");
        String author = scanner.nextLine();
        db.getBooksByAuthor(author);
        return;
    }

    public void getBooksByTitle() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the title you would like to search by: ");
        System.out.print("> ");
        String title = scanner.nextLine();
        db.getBooksByTitle(title);
        return;

    }

    public void getBooksByGenre() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the genre you would like to search by: ");
        System.out.print("> ");
        String genre = scanner.nextLine();
        Map<Integer,Book> map = db.getBooksByGenre(genre);
        return;
    }

    public Loan checkOutBook() {
        return null;
    }

    public boolean logIn() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your email: " );
        System.out.print("> ");
        String username = scanner.nextLine();
        System.out.println("Please enter your password: ");
        System.out.print("> ");
        String password = scanner.nextLine();
        User user = db.getUser(username,password);
        if(user == null) {
            System.out.println("There is no user with those credentials. Please press 1 to try again or press 2 to sign up");
        }else {
            userMenu(user);
        }
        return false;
    }
    public void addUser() {
        Scanner scanner = new Scanner(System.in);
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
    





}
