import java.time.LocalDate;

public class Loan {
    private int loanID;
    private int bookID;
    private int userID;
    private LocalDate checkOutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(int bookID, int userID, LocalDate checkOutDate, int loanDurationDays) {
        this.bookID = bookID;
        this.userID = userID;
        this.checkOutDate = checkOutDate;
        this.dueDate = checkOutDate.plusDays(loanDurationDays);
        this.returnDate = null;
    }

    public void setLoanID(int loanID) {
        this.loanID = loanID;
    }

    public int getLoanID() {
        return loanID;
    }


    public int getBookID() {
        return bookID;
    }

    public int getUserID() {
        return userID;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }
}
