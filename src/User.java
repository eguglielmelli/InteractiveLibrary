import java.util.List;

public class User {

    String name;
    String password;
    String email;
    String contactNumber;
    List<Book> onLoanBooks;
    Database db = new Database();

    public User(String name,String password,String email,List<Book> onLoanBooks,String contactNumber) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.onLoanBooks = onLoanBooks;
        this.contactNumber = contactNumber;
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
