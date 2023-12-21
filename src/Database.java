
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class Database {
    private String url;
    private String user;
    private String password;

    public Database() {
        url = System.getenv("DB_URL");
        user = System.getenv("DB_USER");
        password = System.getenv("DB_PASSWORD");

        if(url == null || user == null || password == null) {
            throw new IllegalStateException("Database credentials are not set in environment variables.");
        }
    }

    /**
     * Method sets up the database and tables if they are not already created
     */
    public void setupDatabase() {

        String databaseName = "library_db";

        String sqlCreateDatabase = "CREATE DATABASE IF NOT EXISTS " + databaseName;

        String sqlCreateBooksInfo = "CREATE TABLE IF NOT EXISTS " + databaseName + ".books_info (" +
                "BookID INT AUTO_INCREMENT PRIMARY KEY, " +
                "Title VARCHAR(255), " +
                "Author VARCHAR(255), " +
                "ISBN VARCHAR(255), " +
                "PublicationYear YEAR, " +
                "Genre VARCHAR(255), " +
                "TotalCopies INT, " +
                "AvailableCopies INT)";

        String sqlCreateCustomersInfo = "CREATE TABLE IF NOT EXISTS " + databaseName + ".customers_info (" +
                "UserID INT AUTO_INCREMENT PRIMARY KEY, " +
                "Name VARCHAR(255), " +
                "Email VARCHAR(255), " +
                "Password VARCHAR(255), " +
                "ContactNumber VARCHAR(255), " +
                "RegistrationDate DATE)";

        String sqlCreateLoansInfo = "CREATE TABLE IF NOT EXISTS " + databaseName + ".loans_info (" +
                "LoanID INT AUTO_INCREMENT PRIMARY KEY, " +
                "BookID INT, " +
                "UserID INT, " +
                "CheckoutDate DATE, " +
                "DueDate DATE, " +
                "ReturnDate DATE, " +
                "FOREIGN KEY (BookID) REFERENCES books_info(BookID), " +
                "FOREIGN KEY (UserID) REFERENCES customers_info(UserID))";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlCreateDatabase);
            System.out.println("Database '" + databaseName + "' created successfully");


            stmt.executeUpdate(sqlCreateBooksInfo);
            System.out.println("Table 'books_info' created successfully");

            stmt.executeUpdate(sqlCreateCustomersInfo);
            System.out.println("Table 'customers_info' created successfully");

            stmt.executeUpdate(sqlCreateLoansInfo);
            System.out.println("Table 'loans_info' created successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * establishes connection to database
     * @return connection
     * @throws SQLException if no connection is made
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }

    /**
     * Adds a book to the database
     * @param title of the book
     * @param author of the book
     * @param ISBN of the book
     * @param year of the book
     * @param genre of the book
     */
    public void addBook(String title,String author,String ISBN,int year,String genre) {
        title = title.toLowerCase(Locale.ROOT);
        author = author.toLowerCase(Locale.ROOT);
        genre = genre.toLowerCase(Locale.ROOT);
        Random random = new Random();
        int totalCopies = 1 + random.nextInt(30);
        int availableCopies = totalCopies;

        String sql = "INSERT INTO library_db.books_info (Title, Author, ISBN, Genre, PublicationYear, TotalCopies, AvailableCopies) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, ISBN);
            pstmt.setString(4, genre);
            pstmt.setInt(5, year);
            pstmt.setInt(6, totalCopies);
            pstmt.setInt(7, availableCopies);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Book added successfully!");
            } else {
                System.out.println("No book was added.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Gets the user from the database after they have provided their credentials
     * @param email of user
     * @param password of user
     * @return user object
     * @throws SQLException if connection cannot be established
     */
    public User getUser(String email,String password) throws SQLException {
        Connection connect = getConnection();
        email = email.trim();
        password = password.trim();
        String sql = "SELECT UserID, Name, Email, Password, ContactNumber, RegistrationDate FROM library_db.customers_info WHERE Email = ? AND Password = ?";
        try (PreparedStatement preparedState = connect.prepareStatement(sql)) {
            preparedState.setString(1,email);
            preparedState.setString(2,password);
            ResultSet rs = preparedState.executeQuery();
            while(rs.next()) {
                int userID = rs.getInt("UserID");
                String name = rs.getString("Name");
                email = rs.getString("Email");
                password = rs.getString("Password");
                String contactNumber = rs.getString("ContactNumber");
                User user = new User(userID,name,password,email,contactNumber);
                return user;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a user to the database after they have provided their information
     * @param name of user
     * @param email of user
     * @param password of user
     * @param contactNumber of user
     * @param registrationDate of user
     * @return true if used was added, else return false
     */
    public boolean addUser(String name, String email, String password, String contactNumber, LocalDate registrationDate) {
        String sql = "INSERT INTO library_db.customers_info (Name, Email, Password, ContactNumber, RegistrationDate) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, contactNumber);
            pstmt.setDate(5, java.sql.Date.valueOf(registrationDate));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a user from the database
     * @param userID ID of user
     * @return true if the user was deleted, else false
     * @throws SQLException if connection couldn't be established
     */
    public boolean deleteUser(int userID) throws SQLException {
        int booksCheckedOut = findNumberOfBooksCheckedOut(userID);
        if (booksCheckedOut != 0) {
            System.out.println("You cannot delete your account while you have books checked out.");
            return false;
        }

        Connection conn = getConnection();
        try {

            conn.setAutoCommit(false);


            String deleteLoansQuery = "DELETE FROM library_db.loans_info WHERE userID = ?";
            try (PreparedStatement pstmtLoans = conn.prepareStatement(deleteLoansQuery)) {
                pstmtLoans.setInt(1, userID);
                pstmtLoans.executeUpdate();
            }


            String deleteUserQuery = "DELETE FROM library_db.customers_info WHERE userID = ?";
            try (PreparedStatement pstmtUser = conn.prepareStatement(deleteUserQuery)) {
                pstmtUser.setInt(1, userID);
                int rowsAffected = pstmtUser.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User successfully deleted.");
                    return true;
                } else {
                    System.out.println("User with that ID could not be found.");
                }
            }


            conn.commit();
        } catch (SQLException e) {

            conn.rollback();
            System.out.println("Error occurred during deletion. Try again.");
            e.printStackTrace();
        } finally {

            conn.setAutoCommit(true);
        }
        return false;
    }

    /**
     * Adds a loan to the database after a user has checked out a book
     * @param loan a new loan created when user checked out a book
     * @return true if the loan was added, else false
     */
    public boolean addLoan(Loan loan) {
        String sql = "INSERT INTO library_db.loans_info (BookID, UserID, CheckoutDate, DueDate, ReturnDate) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, loan.getBookID());
            pstmt.setInt(2, loan.getUserID());
            pstmt.setDate(3, java.sql.Date.valueOf(loan.getCheckOutDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(loan.getDueDate()));

            if (loan.getReturnDate() != null) {
                pstmt.setDate(5, java.sql.Date.valueOf(loan.getReturnDate()));
            } else {
                pstmt.setNull(5, java.sql.Types.DATE);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        loan.setLoanID(generatedKeys.getInt(1)); // Set the auto-generated loan ID
                    }
                }
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Decrements the available copies of a given book after a user has checked out a book
     * @param bookID of the book the user has checked out
     */
    public void decrementAvailableCopies(int bookID) {
        String sql = "UPDATE library_db.books_info SET AvailableCopies = AvailableCopies - 1 WHERE BookID = ? AND AvailableCopies > 0";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookID);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allows the user to search for books using fuzzy search
     * @param searchTerm that the user inputs
     * @return a map of book IDs and book objects that the user can choose to checkout
     * @throws SQLException if connection can't be established
     */
    public Map<String,Book> searchBooks(String searchTerm) throws SQLException {
        Connection connect = getConnection();

        String query = "SELECT * FROM library_db.books_info WHERE " +
                "LOWER(Author) LIKE ? OR LOWER(Title) LIKE ? OR LOWER(Genre) LIKE ? AND AvailableCopies > 0";
        Map<String,Book> result = new HashMap<>();
        searchTerm = "%" + searchTerm.strip().toLowerCase(Locale.ROOT) + "%";

        try (PreparedStatement preparedState = connect.prepareStatement(query)) {
            // Set the search term for each field
            preparedState.setString(1, searchTerm);
            preparedState.setString(2, searchTerm);
            preparedState.setString(3, searchTerm);
            ResultSet rs = preparedState.executeQuery();

            while (rs.next()) {
                System.out.println("-----------------------------");
                int bookID = rs.getInt("BookID");
                String Title = rs.getString("Title");
                String Author = rs.getString("Author");
                String ISBN = rs.getString("ISBN");
                String Genre = rs.getString("Genre");
                int AvailableCopies = rs.getInt("AvailableCopies");
                int TotalCopies = rs.getInt("TotalCopies");
                int year = rs.getInt("PublicationYear");

                System.out.println("BookID: " + bookID);
                System.out.println("Title: " + Title);
                System.out.println("Author: " + Author);
                System.out.println("ISBN: " + ISBN);
                System.out.println("Genre: " + Genre);
                System.out.println("Available Copies: " + AvailableCopies);
                System.out.println("-----------------------------");
                Book book = new Book(bookID,Title,Author,ISBN,year,Genre,TotalCopies,AvailableCopies);
                result.put(String.valueOf(bookID),book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Finds number of books user has checked out
     * @param userID ID of user
     * @return number of books checked out
     * @throws SQLException if connection can't be established
     */
    public int findNumberOfBooksCheckedOut(int userID) throws SQLException {
        Connection connection = getConnection();
        String query = "SELECT COUNT(*) FROM library_db.loans_info WHERE UserID = ? AND ReturnDate IS NULL"; // Assuming ReturnDate is NULL for checked out books
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();


            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Shows the books that are currently checked out by user
     * @param userID ID of user
     * @return a set of loan IDs that a user can check select to return a book
     * @throws SQLException if connection can't be established
     */
    public Set<Integer> showCurrentlyCheckedOutBooks(int userID) throws SQLException {
        Connection connection = getConnection();
        Set<Integer> loanIDs = new HashSet<>();
        String query = "SELECT books_info.*,loans_info.LoanID, loans_info.CheckoutDate, loans_info.DueDate " +
                "FROM books_info " +
                "JOIN loans_info ON books_info.BookID = loans_info.BookID " +
                "WHERE loans_info.UserID = ? AND loans_info.ReturnDate IS NULL";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.isBeforeFirst()) {
                return null;
            }

            System.out.printf("%-15s %-15s %-30s %-30s %-20s %-20s %-15s %-15s %n", "LoanID","BookID",
                    "Title", "Author", "ISBN", "Genre", "Checkout Date", "Due Date");
            System.out.println(String.join("", Collections.nCopies(160, "-")));


            while (resultSet.next()) {
                int bookID = resultSet.getInt("BookID");
                String title = resultSet.getString("Title");
                String author = resultSet.getString("Author");
                String isbn = resultSet.getString("ISBN");
                String genre = resultSet.getString("Genre");

                int loanID = resultSet.getInt("LoanID");
                loanIDs.add(loanID);
                Date checkoutDate = resultSet.getDate("CheckoutDate");
                Date dueDate = resultSet.getDate("DueDate");

                System.out.printf("%-15d %-15d %-30s %-30s %-20s %-20s %-15s %-15s%n",
                        loanID, bookID, title, author, isbn, genre,
                        checkoutDate.toString(), dueDate.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loanIDs;
    }

    /**
     * Increments available copies of a book when a user returns it
     * @param bookID ID of book that is returned
     */
    public void incrementAvailableCopies(int bookID) {
        String sql = "UPDATE library_db.books_info SET AvailableCopies = AvailableCopies + 1 WHERE BookID = ? AND AvailableCopies > 0";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a book to the database by setting the loan return date to the current date
     * @param loanID ID of loan
     * @param returnDate date that user returns book
     * @param userID ID of user
     * @throws SQLException if connection cannot be established
     */
    public void returnBook(int loanID,LocalDate returnDate,int userID) throws SQLException {
        Connection connection = getConnection();
        String query = "UPDATE library_db.loans_info SET ReturnDate = ? WHERE loanID = ? AND userID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(returnDate));
            statement.setInt(2,loanID);
            statement.setInt(3,userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * See users history and the books they have checked out in the past
     * @param userID ID of user
     * @throws SQLException if connection can't be established
     */
    public void seeUsersLoanHistory(int userID) throws SQLException{
        Connection connection = getConnection();
        String query = "SELECT books_info.*,loans_info.LoanID, loans_info.CheckoutDate, loans_info.DueDate,loans_info.ReturnDate " +
                "FROM books_info " +
                "JOIN loans_info ON books_info.BookID = loans_info.BookID " +
                "WHERE loans_info.UserID = ? AND loans_info.ReturnDate IS NOT NULL";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            System.out.printf("%-15s %-15s %-30s %-30s %-20s %-20s %-15s %-15s %-15s %n", "LoanID","BookID",
                    "Title", "Author", "ISBN", "Genre", "Checkout Date", "Due Date","Return Date");
            System.out.println(String.join("", Collections.nCopies(180, "-")));


            while (resultSet.next()) {

                int bookID = resultSet.getInt("BookID");
                String title = resultSet.getString("Title");
                String author = resultSet.getString("Author");
                String isbn = resultSet.getString("ISBN");
                String genre = resultSet.getString("Genre");


                int loanID = resultSet.getInt("LoanID");
                Date checkoutDate = resultSet.getDate("CheckoutDate");
                Date dueDate = resultSet.getDate("DueDate");
                Date returnDate = resultSet.getDate("ReturnDate");


                System.out.printf("%-15d %-15d %-30s %-30s %-20s %-20s %-15s %-15s %-15s %n",
                        loanID, bookID, title, author, isbn, genre,
                        checkoutDate.toString(), dueDate.toString(),returnDate.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
