
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class Database {
    private String url = "jdbc:mysql://localhost:3306/library_db?allowPublicKeyRetrieval=true&useSSL=false";
    private String user = "root";
    private String password = "Eg100997";

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
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }

    public void addBook(String title,String author,String ISBN,int year,String genre) {
        title = title.toLowerCase(Locale.ROOT);
        author = author.toLowerCase(Locale.ROOT);
        genre = genre.toLowerCase(Locale.ROOT);
        Random random = new Random();
        int totalCopies = 1 + random.nextInt(30);
        int availableCopies = 1 + random.nextInt(totalCopies);

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
    public void printBooksTables() {
        String tableName = "books_info"; // Your table name

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM " + tableName;
            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
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
    public boolean addUser(String name, String email, String password, String contactNumber, LocalDate registrationDate) {
        String sql = "INSERT INTO library_db.customers_info (Name, Email, Password, ContactNumber, RegistrationDate) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password); // Ensure this password is hashed
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
    public boolean deleteUser(int userID) throws SQLException {
        // Assume showCurrentlyCheckedOutBooks returns a Set of book IDs
        int booksCheckedOut = findNumberOfBooksCheckedOut(userID);
        if (booksCheckedOut != 0) {
            System.out.println("You cannot delete your account while you have books checked out.");
            return false;
        }

        Connection conn = getConnection();
        try {
            // Start transaction
            conn.setAutoCommit(false);

            // Delete related records from loans_info
            String deleteLoansQuery = "DELETE FROM library_db.loans_info WHERE userID = ?";
            try (PreparedStatement pstmtLoans = conn.prepareStatement(deleteLoansQuery)) {
                pstmtLoans.setInt(1, userID);
                pstmtLoans.executeUpdate();
            }

            // Delete user from customers_info
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

            // Commit transaction
            conn.commit();
        } catch (SQLException e) {
            // Rollback transaction in case of error
            conn.rollback();
            System.out.println("Error occurred during deletion. Try again.");
            e.printStackTrace();
        } finally {
            // Reset default behavior
            conn.setAutoCommit(true);
        }
        return false;
    }
        public void printCustomerTable() {
        String tableName = "customers_info"; // Your table name

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM " + tableName;
            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean addLoan(int bookID, int userID, LocalDate checkOutDate, LocalDate dueDate, LocalDate returnDate) {
        String sql = "INSERT INTO library_db.loans_info (BookID, UserID, CheckoutDate, DueDate, ReturnDate) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookID);
            pstmt.setInt(2, userID);
            pstmt.setDate(3, java.sql.Date.valueOf(checkOutDate));
            pstmt.setDate(4, java.sql.Date.valueOf(dueDate));

            if (returnDate != null) {
                pstmt.setDate(5, java.sql.Date.valueOf(returnDate));
            } else {
                pstmt.setNull(5, java.sql.Types.DATE);
            }

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
    public void printLoansTable() {
        String tableName = "loans_info"; // Your table name

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM " + tableName;
            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void decrementAvailableCopies(int bookID) {
        String sql = "UPDATE library_db.books_info SET AvailableCopies = AvailableCopies - 1 WHERE BookID = ? AND AvailableCopies > 0";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookID);

            int affectedRows = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void clearLoanTable() {
        String sql = "DELETE FROM " + "library_db" + ".loans_info";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Loan table cleared successfully. Rows affected: " + affectedRows);
            } else {
                System.out.println("Loan table is already empty or operation did not execute.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
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
    public int findNumberOfBooksCheckedOut(int userID) throws SQLException {
        Connection connection = getConnection();
        String query = "SELECT COUNT(*) FROM library_db.loans_info WHERE UserID = ? AND ReturnDate IS NULL"; // Assuming ReturnDate is NULL for checked out books
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            // Check if the result set has a row and return the count
            if (resultSet.next()) {
                return resultSet.getInt(1); // The count is in the first column
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no rows are found or an exception occurs
    }
    public Set<Integer> showCurrentlyCheckedOutBooks(int userID) throws SQLException {
        Connection connection = getConnection();
        Set<Integer> bookIDs = new HashSet<>();
        String query = "SELECT books_info.*, loans_info.CheckoutDate, loans_info.DueDate " +
                "FROM books_info " +
                "JOIN loans_info ON books_info.BookID = loans_info.BookID " +
                "WHERE loans_info.UserID = ? AND loans_info.ReturnDate IS NULL";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.isBeforeFirst()) {
                return null;
            }

            System.out.printf("%-15s %-30s %-30s %-20s %-20s %-15s %-15s %n", "BookID",
                    "Title", "Author", "ISBN", "Genre", "Checkout Date", "Due Date");
            System.out.println(String.join("", Collections.nCopies(130, "-")));


            while (resultSet.next()) {
                // Extract book information
                int bookID = resultSet.getInt("BookID");
                bookIDs.add(bookID);
                String title = resultSet.getString("Title");
                String author = resultSet.getString("Author");
                String isbn = resultSet.getString("ISBN");
                String genre = resultSet.getString("Genre");

                // Extract loan information
                Date checkoutDate = resultSet.getDate("CheckoutDate");
                Date dueDate = resultSet.getDate("DueDate");

                // Print each record
                System.out.printf("%-15s %-30s %-30s %-20s %-20s %-15s %-15s %n", bookID,
                        title, author, isbn, genre,
                        checkoutDate.toString(), dueDate.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookIDs;
    }
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
    public void returnBook(int bookID,LocalDate returnDate,int userID) throws SQLException {
        Connection connection = getConnection();
        String query = "UPDATE library_db.loans_info SET ReturnDate = ? WHERE bookID = ? AND userID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(returnDate));
            statement.setInt(2,bookID);
            statement.setInt(3,userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
