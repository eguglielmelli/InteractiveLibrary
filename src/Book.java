/**
 * Class represents a book object where attributes map to the same attributes in the database
 */
public class Book {
    private int bookID;
    private String title;
    private String author;
    private String ISBN;
    private int year;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(int bookID,String title,String author,String ISBN,int year,String genre,int totalCopies,int availableCopies) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.year = year;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getISBN() {
        return ISBN;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    @Override
    public String toString() {
        return getTitle() + " " + getAuthor() + " " + getISBN() + " " + getGenre();
    }

}
