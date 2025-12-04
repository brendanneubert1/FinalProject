/** 
package com.example.library_manager.services;



import java.sql.Connection;
import java.sql.PreparedStatement; 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Service; 

import com.example.library_manager.models.Rating;

@Service
public class RatingService {
    
    private final DataSource dataSource;

    public RatingService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void upsertRating(int userId, String bookId, Double ratingValue) throws SQLException{
        if (ratingValue < 0.0 || ratingValue > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        }

        String sql = """
                INSERT INTO rating (userId, bookId, rating)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE rating = VALUES(rating)
                """;

        try (Connection conn = dataSource.getConnection(); 
                PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, userId); 
                    ps.setString(2, bookId); 
                    ps.setDouble(3, ratingValue); 

                    ps.executeUpdate(); 
                }
            }

        public Optional<Rating> getRatingForUserAndBook(int userId, String bookId) throws SQLException {
            String sql = """
                    SELECT * FROM rating
                    WHERE userId = ? AND bookId = ?
                    """;
            try (Connection conn = dataSource.getConnection(); 
                PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, userId); 
                    ps.setString(2, bookId); 

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            Rating rating = mapRowToRating(rs); 
                            return Optional.of(rating); 
                        } else {
                            return Optional.empty(); 
                        }
                    }
                }
            }
    
        public Double getAverageRatingForBook(String bookId) throws SQLException {
            String sql = """
                    SELECT AVG(rating) AS avg_rating FROM rating
                    WHERE bookId = ?
                    """;

            try (Connection conn = dataSource.getConnection(); 
                PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, bookId); 

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            double avg = rs.getDouble("avg_rating"); 
                            if (rs.wasNull()) {
                                return null; 
                            } 
                            return avg; 
                        } else {
                            return null; 
                        }
                    }
                }
        }

        private Rating mapRowToRating(ResultSet rs) throws SQLException {
            Long ratingId = rs.getLong("ratingId"); 
            int userId = rs.getInt("userId"); 
            String bookId = rs.getString("bookId"); 
            double ratingValue = rs.getDouble("rating"); 

            return new Rating(ratingId, userId, bookId, ratingValue); 
        }
       
    }

    */