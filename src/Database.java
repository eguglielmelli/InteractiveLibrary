import com.mysql.cj.protocol.Resultset;

import java.sql.*;
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

    public void getBooksByAuthor(String author) throws SQLException {

        Connection connect = getConnection();
        String sql = "SELECT BookID, Title, Author, ISBN, Genre, AvailableCopies FROM library_db.books_info WHERE Author = ?";
        author = author.strip().toLowerCase(Locale.ROOT);
        try (PreparedStatement preparedState = connect.prepareStatement(sql)) {
            preparedState.setString(1, author);
            ResultSet rs = preparedState.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("BookID: " + rs.getString("BookID"));
                System.out.println("Title: " + rs.getString("Title"));
                System.out.println("Author: " + rs.getString("Author"));
                System.out.println("ISBN: " + rs.getString("ISBN"));
                System.out.println("Genre: " + rs.getString("Genre"));
                System.out.println("Available Copies: " + rs.getInt("AvailableCopies"));


                System.out.println("-----------------------------");
            }

            if (!found) {
                System.out.println("No books found by author: " + author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public void getBooksByTitle(String title) throws SQLException {
        Connection connect = getConnection();
        title = title.strip().toLowerCase(Locale.ROOT);
        String sql = "SELECT BookID, Title, Author, ISBN, Genre, AvailableCopies FROM library_db.books_info WHERE Title = ?";

        try (PreparedStatement preparedState = connect.prepareStatement(sql)) {
            preparedState.setString(1, title);
            ResultSet rs = preparedState.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("BookID: " + rs.getString("BookID"));
                System.out.println("Title: " + rs.getString("Title"));
                System.out.println("Author: " + rs.getString("Author"));
                System.out.println("ISBN: " + rs.getString("ISBN"));
                System.out.println("Genre: " + rs.getString("Genre"));
                System.out.println("Available Copies: " + rs.getInt("AvailableCopies"));
                System.out.println("-----------------------------");
            }
            if (!found) {
                System.out.println("No books found with title: " + title);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Map<Integer,Book> getBooksByGenre(String genre) throws SQLException {
        Connection connect = getConnection();
        Map<Integer,Book> result = new HashMap<>();
        genre = genre.strip().toLowerCase(Locale.ROOT);
        String sql = "SELECT BookID, Title, Author, ISBN,PublicationYear, Genre,TotalCopies,AvailableCopies FROM library_db.books_info WHERE Genre = ?";

        try (PreparedStatement preparedState = connect.prepareStatement(sql)) {
            preparedState.setString(1, genre);
            ResultSet rs = preparedState.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                int bookID = Integer.parseInt(rs.getString("BookID"));
                String title = rs.getString("Title");
                String author = rs.getString("Author");
                String ISBN = rs.getString("ISBN");
                String Genre = rs.getString("Genre");
                int AvailableCopies = rs.getInt("AvailableCopies");
                int TotalCopies = rs.getInt("TotalCopies");
                int year = rs.getInt("PublicationYear");

                System.out.println("BookID: " + bookID);
                System.out.println("Title: " + title);
                System.out.println("Author: " + author);
                System.out.println("ISBN: " + ISBN);
                System.out.println("Genre: " + Genre);
                System.out.println("Available Copies: " + AvailableCopies);
                System.out.println("-----------------------------");
                Book book = new Book(bookID,title,author,ISBN,year,Genre,TotalCopies,AvailableCopies);
                result.put(bookID,book);

            }
            if (!found) {
                System.out.println("No books found with title: " + genre);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
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
                String name = rs.getString("Name");
                email = rs.getString("Email");
                password = rs.getString("Password");
                String contactNumber = rs.getString("ContactNumber");
                User user = new User(name,password,email,new ArrayList<Book>(),contactNumber);
                return user;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void addUser(String name, String email, String password, String contactNumber, LocalDate registrationDate) {
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
                System.out.println("User added successfully!");
            } else {
                System.out.println("No user was added.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Book selectBookByID(String BookID,User user) {
        return null;
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






}
