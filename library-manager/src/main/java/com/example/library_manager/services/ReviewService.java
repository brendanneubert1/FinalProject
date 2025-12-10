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
import com.example.library_manager.models.Rating;
import com.example.library_manager.models.Review;
import com.example.library_manager.models.User;
import com.example.library_manager.services.BookService;
import com.example.library_manager.services.RatingService;

@Service
@SessionScope
public class ReviewService {
    final DataSource dataSource;
    @Autowired
    public ReviewService(DataSource dataSource) {
        this.dataSource = dataSource;
    }


public boolean makeReview(String userId, String isbn, String reviewContent, boolean recommended) {
    try (Connection conn = dataSource.getConnection()) {
    
        String insertSql =
            "INSERT INTO review (userId, bookId, body, created_date, recommended) " +
            "VALUES (?, ?, ?, NOW(), ?)";
        System.out.println("isbn:" + isbn);
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
    "   r.reviewId, r.userId, u.firstName, u.lastName, r.body, r.recommended, r.created_date, " +
    "   b.isbn, b.title, b.imglink, b.category, b.rating, b.num_ratings, b.release_date AS publishDate " +
    "FROM `review` r " +
    "JOIN book b ON r.bookId = b.isbn " +
    "JOIN `user` u ON u.userId = r.userId " +
    "ORDER BY r.created_date DESC " +
    "LIMIT 20";

    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            BookService bookService = new BookService(dataSource, new RatingService(dataSource));
            Book b = bookService.getBooksByIsbn(rs.getString("isbn"), loggedInUser);
            User u = new User(
                String.valueOf(rs.getInt("userId")),
                rs.getString("firstName"),
                rs.getString("lastName"));

            Review review = new Review(
                String.valueOf(rs.getLong("reviewId")),
                u,
                b,
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