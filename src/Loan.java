import java.time.LocalDate;

public class Loan {
    private int loanID;
    private int bookID;
    private int userID;
    private LocalDate checkOutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(int loanID, int bookID, int userID, LocalDate checkOutDate, int loanDurationDays) {
        this.loanID = loanID;
        this.bookID = bookID;
        this.userID = userID;
        this.checkOutDate = checkOutDate;
        this.dueDate = checkOutDate.plusDays(loanDurationDays);
        this.returnDate = null;
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
