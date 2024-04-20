import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LibraryLMS {
    public List<Book> library;

    public Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/BookLibrary";
        String user = "root";
        String password = "Selva19!";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    public LibraryLMS() {
        library = new ArrayList<>();
        loadLibraryData();
    }

    public List<Book> getLibrary() {
        return library;
    }

    public void loadLibraryData() {
        String sql = "SELECT book_id, title, author, isCheckedOut, dueDate FROM books";
        library.clear();  // Clear the existing list before loading new data
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Book book = Book.fromResultSet(rs);
                library.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error loading library data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addBook(int id, String title, String author) {
        String sql = "INSERT INTO books (book_id, title, author) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, title);
            stmt.setString(3, author);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Book added successfully!");
            } else {
                System.err.println("Failed to add book");
            }
        } catch (SQLException e) {
            System.err.println("SQL error during add book: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void saveLibrary() {
        String updateSQL = "UPDATE books SET title = ?, author = ?, isCheckedOut = ?, dueDate = ? WHERE book_id = ?";
        String insertSQL = "INSERT INTO books (title, author, isCheckedOut, dueDate) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;

        try {
            conn = connect();
            conn.setAutoCommit(false); // Start transaction

            updateStmt = conn.prepareStatement(updateSQL);
            insertStmt = conn.prepareStatement(insertSQL);

            for (Book book : library) {
                if (bookExists(book.getId(), conn)) { // Check if book exists in the database
                    updateStmt.setString(1, book.getTitle());
                    updateStmt.setString(2, book.getAuthor());
                    updateStmt.setBoolean(3, book.isCheckedOut());
                    updateStmt.setString(4, book.getDueDate());
                    updateStmt.setInt(5, book.getId());
                    updateStmt.executeUpdate();
                } else {
                    insertStmt.setString(1, book.getTitle());
                    insertStmt.setString(2, book.getAuthor());
                    insertStmt.setBoolean(3, book.isCheckedOut());
                    insertStmt.setString(4, book.getDueDate());
                    insertStmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            System.err.println("Failed to save library data: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // rollback transaction on error
                } catch (SQLException ex) {
                    System.err.println("Failed to rollback: " + ex.getMessage());
                }
            }
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (updateStmt != null) updateStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (conn != null) conn.setAutoCommit(true); // Reset auto-commit
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.err.println("Error closing connection: " + ex.getMessage());
            }
        }
    }
    public boolean checkOutBook(String title) {
        String sql = "UPDATE books SET isCheckedOut = true, dueDate = ? WHERE title = ? AND isCheckedOut = false";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_YEAR, 4); // Adding 4 weeks to current date for due date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            stmt.setString(1, sdf.format(cal.getTime()));
            stmt.setString(2, title);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error checking out book: " + e.getMessage());
            return false;
        }
    }

    public boolean checkInBook(String title) {
        String sql = "UPDATE books SET isCheckedOut = false, dueDate = NULL WHERE title = ? AND isCheckedOut = true";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0; // Return true if a book was checked in
        } catch (SQLException e) {
            System.err.println("Error checking in book: " + e.getMessage());
            return false;
        }
    }
    public boolean bookExists(int id, Connection conn) throws SQLException {
        String checkSQL = "SELECT 1 FROM books WHERE book_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
            checkStmt.setInt(1, id);
            try (ResultSet rs = checkStmt.executeQuery()) {
                return rs.next(); // Returns true if there is a row, meaning the book exists
            }
        }
    }

    public boolean removeBookById(int id) {
        String checkSql = "SELECT 1 FROM books WHERE book_id = ?";
        String deleteSql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

            // Check if the book exists
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                System.err.println("Book ID not found.");
                return false;
            }

            // Proceed with deletion
            deleteStmt.setInt(1, id);
            int affectedRows = deleteStmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error removing book by ID: " + e.getMessage());
            return false;
        }
    }

    public boolean removeBookByTitle(String title) {
        String sql = "DELETE FROM books WHERE title = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;  // Return true if the book was successfully deleted
        } catch (SQLException e) {
            System.err.println("Error removing book by title: " + e.getMessage());
            return false;
        }
    }

}


