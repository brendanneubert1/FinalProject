package com.example.library_manager.services;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.library_manager.models.Book;
import com.example.library_manager.models.BrowseResult;
import com.example.library_manager.models.ExpandedBook;
import com.example.library_manager.models.PageLink;

import java.util.List;
import java.util.Map;

import com.example.library_manager.models.Author;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



@Service
@SessionScope
public class BookService {
    // dataSource enables talking to the database.
    private final DataSource dataSource;

    @Autowired
    public BookService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /*
    
    */
    public List<Book> testBooks() {
        Author a1 = new Author(1,"Author One");
        Author a2 = new Author(2, "Author Two");
        List<Author> authors = List.of(a1, a2);
        Book b1 = new Book("b1", "Test Book 1", authors, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRk7M1zM4-1jAqcroOVPTmuHEY28xo_0JUOsw&s", "Fiction", null, 4.5f, true);
        return List.of(b1);
    }




    public List<ExpandedBook> testExpandedBooks() {
        final String sql = 
        """
        SELECT 
            b.*,
            a.authorId,
            a.name AS author_name
        FROM book b
        JOIN written_by wb ON b.isbn = wb.bookId
        JOIN author a ON wb.authorId = a.authorId
        LIMIT 5;
        """;

        Map<String, ExpandedBook> bookMap = new LinkedHashMap<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                List<ExpandedBook> books = new ArrayList<>();
                while (rs.next()) {
                    // Extract data from result set and create ExpandedBook objects
                    // Add them to the books list
                    String isbn = rs.getString("isbn");
                    ExpandedBook book = bookMap.get(isbn);
                    if (book == null) {
                        String title = rs.getString("title");
                        String imglink = rs.getString("imglink");
                        String category = rs.getString("category");
                        String description = rs.getString("description");
                        String publishDate = rs.getString("release_date");
                        Float rating = rs.getFloat("rating");
                        int numRatings = rs.getInt("num_ratings");
                        int numPages = rs.getInt("num_pages");

                        book = new ExpandedBook(isbn, title, new ArrayList<>(), imglink, category, publishDate, description, rating, false, false, numRatings, numPages);
                        bookMap.put(isbn,book);
                    }

                    String authorName = rs.getString("author_name");
                    int authorId = rs.getInt("authorId");

                    if (authorName != null) {
                        Author author = new Author(authorId, authorName);
                        book.getAuthors().add(author);
                    }

                }
                return new ArrayList<ExpandedBook>(bookMap.values());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of(); // Return empty list on error
        }
    }



    /*
    returns a page of books for the Browse page
    */
    public BrowseResult getBrowsePage(int page, int size, String title, String author, String category, Float minRating) throws SQLException {

        if (size <= 0) size = 10;
        if (page <= 0) page = 1;

        if (author == "") {author = null; }
        if (category == "") {category = null; }

        int offset = (page - 1) * size;

        // COUNT total books
        final String countSql = "SELECT COUNT(*) AS total FROM book";

        int totalBooks = 0;
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(countSql);
            ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                totalBooks = rs.getInt("total");
            }
        }

        int totalPages = (int) Math.ceil(totalBooks / (double) size);
        if (totalPages < 1) totalPages = 1;

        // Clamp page to valid range
        if (page > totalPages) page = totalPages;

        offset = (page - 1) * size;

        System.out.println("Page=" + page + " Size=" + size + " Offset=" + offset);


        // PAGE QUERY BOOKS ONLY 
        final String booksSql = """
SELECT DISTINCT
    b.isbn,
    b.title,
    b.imglink,
    b.category,
    b.description,
    b.release_date AS publishDate,
    b.rating,
    b.num_ratings,
    b.num_pages
FROM book b
LEFT JOIN written_by wb ON b.isbn = wb.bookId
LEFT JOIN author a ON wb.authorId = a.authorId
WHERE (? IS NULL OR b.title LIKE CONCAT('%', ?, '%'))
  AND (? IS NULL OR b.category LIKE CONCAT('%', ?, '%'))
  AND (? IS NULL OR a.name LIKE CONCAT('%', ?, '%'))
  AND b.rating > ?
ORDER BY b.title
LIMIT ? OFFSET ?;
""";

        Map<String, ExpandedBook> map = new LinkedHashMap<>();

        List<String> pageIsbns = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(booksSql)) {
            
            if (title == "") {stmt.setNull(1, Types.VARCHAR);} else {stmt.setString(1, title);}
            stmt.setString(2, title);
            if (category == "") {stmt.setNull(3, Types.VARCHAR);} else {stmt.setString(3, category);}
            stmt.setString(4, category);
            if (author == "") {stmt.setNull(5, Types.VARCHAR);} else {stmt.setString(5, author);}
            stmt.setString(6, author);
            stmt.setFloat(7, minRating);
            stmt.setInt(8, size);
            stmt.setInt(9, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    String isbn = rs.getString("isbn");
                    pageIsbns.add(isbn);

                    ExpandedBook book = new ExpandedBook(
                        isbn,
                        rs.getString("title"),
                        new ArrayList<>(), // empty authors for now
                        rs.getString("imglink"),
                        rs.getString("category"),
                        rs.getString("publishDate"),
                        rs.getString("description"),
                        rs.getFloat("rating"),
                        false,
                        false,
                        rs.getInt("num_ratings"),
                        rs.getInt("num_pages")
                    );

                    map.put(isbn, book);
                }
            }
        }

        // return empty if no books in page
        if (pageIsbns.isEmpty()) {
            return new BrowseResult(
                    List.of(),
                    page,
                    size,
                    totalPages,
                    page > 1,
                    page < totalPages,
                    Math.max(1, page - 1),
                    Math.min(totalPages, page + 1),
                    List.of()
            );
        }


        // Get authors for the books
        String placeholders = String.join(",", java.util.Collections.nCopies(pageIsbns.size(), "?"));

        String authorsSql = 
        "SELECT wb.bookId, a.authorId, a.name AS author_name " + 
        "FROM written_by wb " +
        "JOIN author a ON wb.authorId = a.authorId " +
        "WHERE wb.bookId IN (" + placeholders + ")" +
        " AND (? IS NULL OR a.name LIKE CONCAT('%', ?, '%')); ";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(authorsSql)) {
            
            int index = 1;
            for (String isbn : pageIsbns) {
                stmt.setString(index++, isbn);
            }
            if (author == "") {stmt.setNull(index++, Types.VARCHAR);} else {stmt.setString(index++, author);}
            stmt.setString(index++, author);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String bookId = rs.getString("bookId");

                    ExpandedBook book = map.get(bookId);
                    if (book != null) {
                        book.getAuthors().add(new Author(
                                rs.getInt("authorId"),
                                rs.getString("author_name")
                        ));
                    }
                }
            }
        }


        // Pagination
        boolean hasPrev = page > 1;
        boolean hasNext = page < totalPages;
        int prevPage = hasPrev ? page - 1 : 1;
        int nextPage = hasNext ? page + 1 : totalPages;

        // Page windows
        int maxshown = 9;
        int startPage = Math.max(1,page-maxshown /2);
        int endPage = startPage + maxshown;

        if (endPage > totalPages) {
            endPage = totalPages;
            startPage = Math.max(1, endPage - maxshown + 1);
        }

        List<PageLink> pages = new ArrayList<>();
        for (int p = startPage; p <= endPage; p++) {
            
        }

        System.out.printf(
    "Pagination: page=%d size=%d totalPages=%d hasPrev=%b hasNext=%b pagesSize=%d%n",
            page, size, totalPages, hasPrev, hasNext, pages.size()
        );

        return new BrowseResult(
                new ArrayList<>(map.values()),
                page,
                size,
                totalPages,
                hasPrev,
                hasNext,
                prevPage,
                nextPage,
                pages
        );
    }








    public boolean wishlistBook(String userId, String bookId) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean likeBook(String userId, String bookId) {
        // TODO Auto-generated method stub
        return false;
    }




}
