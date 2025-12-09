package com.example.library_manager.services;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.library_manager.models.Book;
import com.example.library_manager.models.ExpandedBook;

import java.util.List;
import java.util.Map;

import com.example.library_manager.models.Author;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;




@Service
@SessionScope
public class BookService {
    // dataSource enables talking to the database.
    private final DataSource dataSource;

    @Autowired
    public BookService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    
    public boolean wishlistBook(String userId, String bookId) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean likeBook(String userId, String bookId) {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean markBookAsRead(String userId, String bookId) {
        // TODO Auto-generated method stub
        return false;
    }




    public List<ExpandedBook> getReadBooks(String userId) throws SQLException{        
        final String sql = 
        """
SELECT
	B.isbn,
	B.title,
    B.description,
    B.imglink,
    B.category,
    B.rating,
    B.num_ratings,
    B.num_pages,
	B.release_date,
	A.authorId,
    A.name AS author_name,
	COUNT(DISTINCT L.userId) AS likes_count,
    MAX(CASE WHEN 1 = L.userId  THEN 1 ELSE 0 END) AS liked,
    MAX(CASE WHEN 1 = W.userId  THEN 1 ELSE 0 END) AS wishlisted,
    MAX(CASE WHEN 1 = R.userId  THEN 1 ELSE 0 END) AS `read`
FROM book B
LEFT JOIN written_by WB ON WB.bookId = B.isbn
LEFT JOIN author A ON A.authorId = WB.authorId
LEFT JOIN likes as L ON b.isbn = L.bookId
LEFT JOIN wishlist as W ON B.isbn = W.bookId
LEFT JOIN `read` as R ON B.isbn = R.bookId
WHERE R.userId = ?
GROUP BY B.isbn, A.authorId;       
        """;


        Map<String, ExpandedBook> bookMap = new LinkedHashMap<>(); // preserves order
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String isbn = rs.getString("isbn");
                    ExpandedBook book = bookMap.get(isbn);
                    if (book == null) {
                        book = new ExpandedBook(
                            rs.getString("isbn"),
                            rs.getString("title"),
                            new ArrayList<>(), // empty authors for now
                            rs.getString("imglink"),
                            rs.getString("category"),
                            rs.getString("release_date"),
                            rs.getFloat("rating"),
                            rs.getInt("num_ratings"),
                            rs.getInt("likes_count"),
                            rs.getBoolean("liked"),
                            rs.getBoolean("wishlisted"),
                            rs.getBoolean("read"),
                            rs.getInt("num_pages"),
                            rs.getString("description")
                        );
                        bookMap.put(isbn, book);
                    }
                    book.getAuthors().add(new Author(
                        rs.getInt("authorId"),
                        rs.getString("author_name")
                    ));
                }
            }
        }

        return new ArrayList<>(bookMap.values());
    }



    public List<ExpandedBook> getWishlistedBooks(String userId) throws SQLException{
        final String sql = 
        """
SELECT
	B.isbn,
	B.title,
    B.description,
    B.imglink,
    B.category,
    B.rating,
    B.num_ratings,
    B.num_pages,
	B.release_date,
	A.authorId,
    A.name AS author_name,
	COUNT(DISTINCT L.userId) AS likes_count,
    MAX(CASE WHEN 1 = L.userId  THEN 1 ELSE 0 END) AS liked,
    MAX(CASE WHEN 1 = W.userId  THEN 1 ELSE 0 END) AS wishlisted,
    MAX(CASE WHEN 1 = R.userId  THEN 1 ELSE 0 END) AS `read`
FROM book B
LEFT JOIN written_by WB ON WB.bookId = B.isbn
LEFT JOIN author A ON A.authorId = WB.authorId
LEFT JOIN likes as L ON b.isbn = L.bookId
LEFT JOIN wishlist as W ON B.isbn = W.bookId
LEFT JOIN `read` as R ON B.isbn = R.bookId
WHERE W.userId = ?
GROUP BY B.isbn, A.authorId;       
        """;


        Map<String, ExpandedBook> bookMap = new LinkedHashMap<>(); // preserves order
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String isbn = rs.getString("isbn");
                    ExpandedBook book = bookMap.get(isbn);
                    if (book == null) {
                        book = new ExpandedBook(
                            rs.getString("isbn"),
                            rs.getString("title"),
                            new ArrayList<>(), // empty authors for now
                            rs.getString("imglink"),
                            rs.getString("category"),
                            rs.getString("release_date"),
                            rs.getFloat("rating"),
                            rs.getInt("num_ratings"),
                            rs.getInt("likes_count"),
                            rs.getBoolean("liked"),
                            rs.getBoolean("wishlisted"),
                            rs.getBoolean("read"),
                            rs.getInt("num_pages"),
                            rs.getString("description")
                        );
                        bookMap.put(isbn, book);
                    }
                    book.getAuthors().add(new Author(
                        rs.getInt("authorId"),
                        rs.getString("author_name")
                    ));
                }
            }
        }

        return new ArrayList<>(bookMap.values());
    }


    public List<Book> searchBooksByTitle(String title) throws SQLException{
        // TODO Auto-generated method stub
        final String sql = """
        SELECT 
        isbn, title, A.name, A.authorId, imglink, category 
        FROM book 
        LEFT JOIN written_by WB ON WB.bookId = book.isbn
        LEFT JOIN author A ON A.authorId = WB.authorId
        WHERE title LIKE CONCAT('%', ?, '%');
        """;

        Map<String, Book> bookMap = new LinkedHashMap<>(); // preserves order
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String isbn = rs.getString("isbn");
                    Book book = bookMap.get(isbn);
                    if (book == null) {
                        book = new Book(
                            rs.getString("isbn"),
                            rs.getString("title"),
                            new ArrayList<>(), // empty authors for now
                            rs.getString("imglink")
                        );
                        bookMap.put(isbn, book);
                    }
                    book.getAuthors().add(new Author(
                        rs.getInt("authorId"),
                        rs.getString("name")
                    ));
                }
            }
        }

        return new ArrayList<>(bookMap.values());
    }

}
