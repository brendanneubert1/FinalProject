package com.example.library_manager.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    // === CONFIG ===
    // If Java is running on your host and Docker exposes 3306 → use localhost.
    // If Java is in another container on same network, use "mysql-server-4370" instead of "localhost".
    private static final String DB_URL =
            "jdbc:mysql://localhost:33306/library_manager";

    private static final String DB_USER = "root";      // <-- change if needed
    private static final String DB_PASS = "mysqlpass"; // <-- change

    // Max length allowed by your `book.title` column (e.g. VARCHAR(255))
    private static final int MAX_TITLE_LENGTH = 255;

    // Output file for truncated titles
    private static final String TRUNCATED_TITLES_REPORT = "truncated_titles_report.csv";

    // Insert or update into book (your schema)
    private static final String INSERT_BOOK = """
        INSERT INTO book (
            isbn,
            title,
            imglink,
            category,
            description,
            rating,
            num_ratings,
            num_pages,
            release_date
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            title        = VALUES(title),
            imglink      = VALUES(imglink),
            category     = VALUES(category),
            description  = VALUES(description),
            rating       = VALUES(rating),
            num_ratings  = VALUES(num_ratings),
            num_pages    = VALUES(num_pages),
            release_date = VALUES(release_date)
        """;

    // Insert author by name.
    private static final String INSERT_AUTHOR = """
        INSERT INTO author (name)
        VALUES (?)
        """;

    private static final String SELECT_AUTHOR_BY_NAME = """
        SELECT authorId FROM author WHERE name = ?
        """;

    // Join table written_by(bookId, authorId)
    private static final String INSERT_WRITTEN_BY = """
        INSERT IGNORE INTO written_by (bookId, authorId)
        VALUES (?, ?)
        """;

    // Simple holder for truncated title info
    private static class TruncatedTitle {
        final String isbn;
        final int originalLength;
        final String originalTitle;
        final String truncatedTitle;

        TruncatedTitle(String isbn, int originalLength, String originalTitle, String truncatedTitle) {
            this.isbn = isbn;
            this.originalLength = originalLength;
            this.originalTitle = originalTitle;
            this.truncatedTitle = truncatedTitle;
        }
    }

    public static void main(String[] args) {
        // TODO: update this path to where your CSV actually lives
        String csvPath = "library-manager/books.csv";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);

            try {
                importCsv(conn, csvPath);
                System.out.println("CSV imported successfully.");
                conn.commit();
                System.out.println("Import completed successfully.");
            } catch (Exception e) {
                conn.rollback();
                System.err.println("Import failed, rolled back.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("DB connection failed:");
            e.printStackTrace();
        }
    }

    /**
     * Main importer: reads the CSV and populates book, author, written_by.
     */
    private static void importCsv(Connection conn, String path)
            throws IOException, CsvException, SQLException {

        List<TruncatedTitle> truncatedTitles = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(path));
             PreparedStatement bookStmt = conn.prepareStatement(INSERT_BOOK);
             PreparedStatement insertAuthorStmt = conn.prepareStatement(INSERT_AUTHOR, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement selectAuthorStmt = conn.prepareStatement(SELECT_AUTHOR_BY_NAME);
             PreparedStatement writtenByStmt = conn.prepareStatement(INSERT_WRITTEN_BY)) {

            List<String[]> rows = reader.readAll();
            if (rows.isEmpty()) {
                System.out.println("CSV is empty.");
                return;
            }

            String[] header = rows.get(0);
            int idxIsbn13       = findIndex(header, "isbn13");
            int idxTitle        = findIndex(header, "title");
            int idxAuthors      = findIndex(header, "authors");
            int idxCategories   = findIndex(header, "categories");
            int idxThumbnail    = findIndex(header, "thumbnail");
            int idxDescription  = findIndex(header, "description");
            int idxYear         = findIndex(header, "published_year");
            int idxAvgRating    = findIndex(header, "average_rating");
            int idxNumPages     = findIndex(header, "num_pages");
            int idxRatingsCount = findIndex(header, "ratings_count");
            // We ignore: isbn10, subtitle

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row == null || row.length == 0) {
                    continue;
                }

                String isbn          = safeCell(row, idxIsbn13);
                String rawTitle      = safeCell(row, idxTitle);
                String authorsStr    = safeCell(row, idxAuthors);
                String categories    = safeCell(row, idxCategories);
                String thumbnail     = safeCell(row, idxThumbnail);
                String description   = safeCell(row, idxDescription);
                String yearStr       = safeCell(row, idxYear);
                String ratingStr     = safeCell(row, idxAvgRating);
                String numPagesStr   = safeCell(row, idxNumPages);
                String numRatingsStr = safeCell(row, idxRatingsCount);

                if (isbn.isEmpty()) {
                    System.out.printf("Row %d has empty isbn13, skipping.%n", i + 1);
                    continue;
                }

                // === Handle title truncation ===
                String title = rawTitle;
                if (rawTitle != null && rawTitle.length() > MAX_TITLE_LENGTH) {
                    String truncated = rawTitle.substring(0, MAX_TITLE_LENGTH);
                    truncatedTitles.add(
                            new TruncatedTitle(isbn, rawTitle.length(), rawTitle, truncated)
                    );
                    title = truncated;
                }

                Integer publishedYear = parseInteger(yearStr);
                Float rating = parseFloat(ratingStr);
                Integer numPages = parseInteger(numPagesStr);
                Integer numRatings = parseInteger(numRatingsStr);

                // Map published_year → release_date as YYYY-01-01
                Date releaseDate = null;
                if (publishedYear != null && publishedYear > 0) {
                    releaseDate = Date.valueOf(publishedYear + "-01-01");
                }

                // === Insert/Update into book ===
                bookStmt.setString(1, isbn);
                bookStmt.setString(2, title);
                bookStmt.setString(3, thumbnail);   // imglink
                bookStmt.setString(4, categories);  // category (raw CSV categories string)
                bookStmt.setString(5, description);

                if (rating == null) {
                    bookStmt.setNull(6, Types.DECIMAL);
                } else {
                    bookStmt.setFloat(6, rating);
                }

                if (numRatings == null) {
                    bookStmt.setNull(7, Types.INTEGER);
                } else {
                    bookStmt.setInt(7, numRatings);
                }

                if (numPages == null) {
                    bookStmt.setNull(8, Types.INTEGER);
                } else {
                    bookStmt.setInt(8, numPages);
                }

                if (releaseDate == null) {
                    bookStmt.setNull(9, Types.DATE);
                } else {
                    bookStmt.setDate(9, releaseDate);
                }

                bookStmt.executeUpdate();

                // === Authors + written_by ===
                List<String> authors = splitMulti(authorsStr);
                for (String authorName : authors) {
                    int authorId = getOrCreateAuthorId(selectAuthorStmt, insertAuthorStmt, authorName);
                    writtenByStmt.setString(1, isbn);     // bookId
                    writtenByStmt.setInt(2, authorId);    // authorId
                    writtenByStmt.executeUpdate();
                }

                if (i % 1000 == 0) {
                    System.out.println("Processed " + i + " data rows so far...");
                }
            }

            System.out.println("Processed " + (rows.size() - 1) + " data rows.");

            // After import, write truncated titles report (if any)
            writeTruncatedTitlesReport(truncatedTitles);
        }
    }

    // ===== Helper methods =====

    private static int findIndex(String[] header, String name) {
        for (int i = 0; i < header.length; i++) {
            if (header[i] != null && header[i].trim().equalsIgnoreCase(name)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Column not found in CSV header: " + name);
    }

    private static String safeCell(String[] row, int idx) {
        if (idx < 0 || idx >= row.length) return "";
        String v = row[idx];
        return v == null ? "" : v.trim();
    }

    private static Integer parseInteger(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Float parseFloat(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Splits authors string like "Author One; Author Two" or "A, B" into list.
     */
    private static List<String> splitMulti(String s) {
        List<String> out = new ArrayList<>();
        if (s == null || s.isBlank()) return out;

        // Split on ; or , (adjust if your data uses something else)
        String[] parts = s.split("[;,]");
        for (String part : parts) {
            String t = part.trim();
            if (!t.isEmpty()) {
                out.add(t);
            }
        }
        return out;
    }

    /**
     * Reuse existing author by name if present, otherwise insert new.
     */
    private static int getOrCreateAuthorId(PreparedStatement selectStmt,
                                           PreparedStatement insertStmt,
                                           String name) throws SQLException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Empty author name");
        }

        // First, try to find existing author
        selectStmt.setString(1, name);
        try (ResultSet rs = selectStmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("authorId");
            }
        }

        // Not found → insert
        insertStmt.setString(1, name);
        insertStmt.executeUpdate();

        try (ResultSet rs = insertStmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Failed to get generated authorId for name: " + name);
            }
        }
    }

    /**
     * Writes a CSV file listing all titles that were truncated.
     */
    private static void writeTruncatedTitlesReport(List<TruncatedTitle> truncatedTitles) {
        if (truncatedTitles.isEmpty()) {
            System.out.println("No titles were truncated.");
            return;
        }

        System.out.println("Writing truncated titles report to: " + TRUNCATED_TITLES_REPORT);

        try (PrintWriter pw = new PrintWriter(new FileWriter(TRUNCATED_TITLES_REPORT))) {
            // header
            pw.println("isbn,original_length,original_title,truncated_title");

            for (TruncatedTitle t : truncatedTitles) {
                // Very basic CSV escaping: replace quotes with doubled quotes, wrap in quotes
                String origEsc = "\"" + t.originalTitle.replace("\"", "\"\"") + "\"";
                String truncEsc = "\"" + t.truncatedTitle.replace("\"", "\"\"") + "\"";

                pw.printf("%s,%d,%s,%s%n",
                        t.isbn,
                        t.originalLength,
                        origEsc,
                        truncEsc
                );
            }
        } catch (IOException e) {
            System.err.println("Failed to write truncated titles report:");
            e.printStackTrace();
        }
    }
}
