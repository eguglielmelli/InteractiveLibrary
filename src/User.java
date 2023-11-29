import java.util.List;

public class User {
    int userID;
    String name;
    String password;
    String email;
    String contactNumber;
    List<Book> onLoanBooks;
    Database db = new Database();

    public User(int userID,String name,String password,String email,List<Book> onLoanBooks,String contactNumber) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.email = email;
        this.onLoanBooks = onLoanBooks;
        this.contactNumber = contactNumber;
    }
    public int getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public List<Book> getOnLoanBooks() {
        return onLoanBooks;
    }

}
