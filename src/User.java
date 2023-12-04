
public class User {
    private int userID;
    private String name;
    private String password;
    private String email;
    private String contactNumber;

    public User(int userID,String name,String password,String email,String contactNumber) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.email = email;
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

}
