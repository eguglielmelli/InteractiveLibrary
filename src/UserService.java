import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class UserService {

    private Database db = new Database();
    private String email = null;
    private String password = null;

    public void seeAvailableBooks() {
        db.printBooksTables();
    }

    public void userMenu(User user) throws SQLException {
        System.out.println("Welcome " + user.getName() + "! Please choose from these search options " +
                "\n 1. Search by Author " +
                "\n 2. Search by Genre " +
                "\n 3. Search by Title " +
                "\n 4. See Checked Out Books" +
                "\n 5. Log out");

        Scanner scanner = new Scanner(System.in);
        System.out.print("> ");
        String choice = scanner.nextLine();
        while(!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4") && !choice.equals("5")) {
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
            case "4":
                seeBooksOnLoan();
                break;
            case "5":
                Display ds = new Display();
                ds.displayMenu();
        }
    }

    public void getBooksByAuthor() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        User user = db.getUser(email,password);
        System.out.println("Please enter the author you would like to search by: ");
        System.out.print("> ");
        String author = scanner.nextLine();
        Map<String,Book> bookMap = db.getBooksByAuthor(author);
        System.out.println("Select an ID of the book you would like to check out, or press q to return to the options menu");
        System.out.print("> ");
        String bookChoice = scanner.nextLine();
        if(bookChoice.equals("q")) {
            userMenu(db.getUser(email,password));
            return;
        }
        while(!bookMap.containsKey(bookChoice)) {
            System.out.println("That is not a valid ID. Please choose from the list.");
            System.out.print("> ");
            bookChoice = scanner.nextLine();
            if(bookChoice.equals("q")) {
                userMenu(db.getUser(email,password));
            }
        }
        checkOutBook(bookMap,bookChoice);
        user.getOnLoanBooks().add(bookMap.get(bookChoice));

        return;
    }

    public void getBooksByTitle() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        User user = db.getUser(email,password);
        System.out.println("Please enter the title you would like to search by: ");
        System.out.print("> ");
        String title = scanner.nextLine();

        Map<String,Book> bookMap = db.getBooksByTitle(title);
        System.out.println("Select an ID of the book you would like to check out, or press q to return to the options menu");
        System.out.print("> ");
        String bookChoice = scanner.nextLine();
        if(bookChoice.equals("q")) {
            userMenu(db.getUser(email,password));
            return;
        }
        while(!bookMap.containsKey(bookChoice)) {
            System.out.println("That is not a valid ID. Please choose from the list.");
            System.out.print("> ");
            bookChoice = scanner.nextLine();
            if(bookChoice.equals("q")) {
                userMenu(db.getUser(email,password));
            }
        }
        checkOutBook(bookMap,bookChoice);
        System.out.println(user.userID);

        return;

    }

    public void getBooksByGenre() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the genre you would like to search by: ");
        System.out.print("> ");
        String genre = scanner.nextLine();
        Map<String,Book> map = db.getBooksByGenre(genre);
        return;
    }

    public void checkOutBook(Map<String,Book> map,String bookID) throws SQLException {
        User user = db.getUser(email,password);
        db.addLoan(Integer.parseInt(bookID),user.getUserID(),LocalDate.now(),LocalDate.now().plusDays(30),null);
        user.onLoanBooks.add(map.get(bookID));
        db.decrementAvailableCopies(Integer.parseInt(bookID));
        return;

    }

    public boolean logIn() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your email: " );
        System.out.print("> ");
        email = scanner.nextLine();
        System.out.println("Please enter your password: ");
        System.out.print("> ");
        password = scanner.nextLine();
        User user = db.getUser(email,password);
        this.email = user.email;
        this.password = user.password;
        if(user == null) {
            System.out.println("There is no user with those credentials. Choose from these options: " +
                    "\n 1. Try again" +
                    "\n 2. Sign up" +
                    "\n 3. Press 'q' to quit");
            System.out.print("> ");
            String choice = scanner.nextLine().toLowerCase(Locale.ROOT);
            while(!choice.equals("1") && !choice.equals("2") && !choice.equals("q")) {
                System.out.println("Invalid option. Please choose again.");
                System.out.print("> ");
                choice = scanner.nextLine();
            }

            if(choice.equals("1")) {
                logIn();
            }
            else if(choice.equals("2")) {
                addUser();
            }
            else if(choice.equals("q")) {
                Display display = new Display();
                display.displayMenu();
            }
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
    public void seeBooksOnLoan() throws SQLException {
        User user = db.getUser(email,password);
        if(!user.getOnLoanBooks().isEmpty()) {
            System.out.println("Here are your books currently checked out: ");
            for(Book book : user.getOnLoanBooks()) {
                System.out.println(book.toString());
            }
        }else {
            System.out.println("No books currently checked out.");
        }
    }

    





}
