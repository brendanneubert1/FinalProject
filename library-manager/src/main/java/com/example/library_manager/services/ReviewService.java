package com.example.library_manager.services;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.example.library_manager.models.Book;
import com.example.library_manager.models.Review;

@Service
@SessionScope
public class ReviewService {
    final DataSource dataSource;
    @Autowired
    public ReviewService(DataSource dataSource) {
        this.dataSource = dataSource;
    }


public boolean makeReview(String userId, String reviewContent, String bookTitle, boolean recommended) {
    try (Connection conn = dataSource.getConnection()) {

        // 1. Look up ISBN based on book title
        String isbn = null;
        String findBookSql = "SELECT isbn FROM book WHERE title = ?";
        try (PreparedStatement ps = conn.prepareStatement(findBookSql)) {
            ps.setString(1, bookTitle);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                isbn = rs.getString("isbn");
            } else {
                return false; // No book found
            }
        }

      
        String insertSql =
            "INSERT INTO review (userId, bookId, body, created_date, recommended) " +
            "VALUES (?, ?, ?, NOW(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, Integer.parseInt(userId));
            ps.setString(2, isbn);
            ps.setString(3, reviewContent);
            ps.setBoolean(4, recommended);
            ps.executeUpdate();
        }

        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


   
public List<Review> getHomeReviews(String loggedInUser) throws SQLException {
    List<Review> reviews = new ArrayList<>();

    String sql =
    "SELECT " +
    "   r.reviewId, r.userId, r.body, r.recommended, r.created_date, " +
    "   b.isbn, b.title, b.imglink, b.category, b.rating, b.num_ratings, b.release_date AS publishDate " +
    "FROM `review` r " +
    "JOIN book b ON r.bookId = b.isbn " +
    "ORDER BY r.created_date DESC " +
    "LIMIT 20";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

          

    Book book = new Book(
        rs.getString("isbn"),     // isbn
        rs.getString("title"),    // title
        new ArrayList<>(),        
       rs.getString("imglink"),
        rs.getString("category"),
        rs.getString("publishDate"), // publishDate
        0.0f,                     // rating (default)
        0,                        // numRatings (default)
        0,                        // heartsCount (default)
        false,                    // isHearted
        false,                    // isWishlisted
        false                     
    );

    Review review = new Review(
        String.valueOf(rs.getLong("reviewId")),
        String.valueOf(rs.getInt("userId")),
        book,
         rs.getString("body"),
                rs.getBoolean("recommended"),
                rs.getTimestamp("created_date").toLocalDateTime()
            );
            reviews.add(review);

}
        }
    

    return reviews;
}



}