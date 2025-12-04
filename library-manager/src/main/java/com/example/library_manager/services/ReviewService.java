package com.example.library_manager.services;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.example.library_manager.models.Review;

@Service
@SessionScope
public class ReviewService {
    final DataSource dataSource;
    @Autowired
    public ReviewService(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public boolean makeReview(String userId, String reviewContent, String bookTitle, int rating, boolean reccommended) {
        // TODO Auto-generated method stub
        // FIRST search for book by title to get bookId (IF TITLE DOESNT MATCH ISBN RETURN FALSE)
        // THEN create review with review object attributes
        // USE the shorter Book constructor for reviews
        return false;
    }

    public List<Review> getHomeReviews(String loggedinUser) throws SQLException {
        // TODO Auto-generated method stub
        // use shorter Book constructor for reviews
        return new ArrayList<>();
    }

}