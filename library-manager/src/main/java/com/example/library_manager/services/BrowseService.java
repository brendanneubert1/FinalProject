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
public class BrowseService {
    // dataSource enables talking to the database.
    private final DataSource dataSource;

    @Autowired
    public BrowseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /*
    returns a page of books for the Browse page
    */
    public BrowseResult getBrowsePage(String loggedInUser, int page, int size, String title, String author, String category, Float minRating) throws SQLException {

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
         SELECT
            b.isbn,
            b.title,
            b.imglink,
            b.category,
            b.description,
            b.release_date AS publishDate,
            b.rating,
            b.num_ratings,
            b.num_pages,
            COUNT(DISTINCT L.userId) AS likes_count,
            MAX(CASE WHEN ? = L.userId  THEN 1 ELSE 0 END) AS liked,
            MAX(CASE WHEN ? = W.userId  THEN 1 ELSE 0 END) AS wishlisted,
            MAX(CASE WHEN ? = R.userId  THEN 1 ELSE 0 END) AS `read`
        FROM book b
        LEFT JOIN written_by wb ON b.isbn = wb.bookId
        LEFT JOIN author a ON wb.authorId = a.authorId
        LEFT JOIN likes as L ON b.isbn = L.bookId
        LEFT JOIN wishlist as W ON b.isbn = W.bookId
        LEFT JOIN `read` as R ON b.isbn = R.bookId 
        WHERE (? IS NULL OR b.title LIKE CONCAT('%', ?, '%'))
        AND (? IS NULL OR b.category LIKE CONCAT('%', ?, '%'))
        AND (? IS NULL OR a.name LIKE CONCAT('%', ?, '%'))
        AND b.rating > ?
        GROUP BY b.isbn
        ORDER BY b.title
        LIMIT ? OFFSET ?;
        """;

        Map<String, ExpandedBook> map = new LinkedHashMap<>();

        List<String> pageIsbns = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(booksSql)) {
            
            stmt.setString(1, loggedInUser);
            stmt.setString(2, loggedInUser);
            stmt.setString(3, loggedInUser);      
            if (title == "") {stmt.setNull(4, Types.VARCHAR);} else {stmt.setString(4, title);}
            stmt.setString(5, title);
            if (category == "") {stmt.setNull(6, Types.VARCHAR);} else {stmt.setString(6, category);}
            stmt.setString(7, category);
            if (author == "") {stmt.setNull(8, Types.VARCHAR);} else {stmt.setString(8, author);}
            stmt.setString(9, author);
            stmt.setFloat(10, minRating);
            stmt.setInt(11, size);
            stmt.setInt(12, offset);

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
                        rs.getFloat("rating"),
                        rs.getInt("num_ratings"),
                        rs.getInt("likes_count"),
                        rs.getBoolean("liked"),
                        rs.getBoolean("wishlisted"),
                        rs.getBoolean("read"),
                        rs.getInt("num_pages"),
                        rs.getString("description")
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
        int startPage = Math.max(1, page-maxshown /2);
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







}
