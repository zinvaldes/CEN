import java.sql.ResultSet;
import java.sql.SQLException;

public class Book {
    public final int id;
    public String title;
    public String author;
    public boolean checkedOut;
    public String dueDate;

    // Constructor
    public Book(int id, String title, String author, boolean checkedOut) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.checkedOut = checkedOut;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public boolean isCheckedOut() { return checkedOut; }
    public void setCheckedOut(boolean checkedOut) { this.checkedOut = checkedOut; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    // Book object from a database row
    public static Book fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("book_id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        boolean isCheckedOut = rs.getBoolean("isCheckedOut");
        String dueDate = rs.getString("dueDate");
        Book book = new Book(id, title, author, isCheckedOut);
        book.setDueDate(dueDate);
        return book;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Title: " + title + ", Author: " + author +
                ", Status: " + (checkedOut ? "Checked out, Due: " + dueDate : "Available");
    }
}
