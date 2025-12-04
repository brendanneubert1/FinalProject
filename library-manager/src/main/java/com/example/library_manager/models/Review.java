package com.example.library_manager.models;

import com.example.library_manager.models.Book;
import com.example.library_manager.models.ExpandedBook;
import java.time.LocalDateTime;
import com.example.library_manager.models.User;


/**
 * Represents a review made by a user for a book.
 */



public class Review {
    private String reviewId;
    private User user;
    private Book book;
    private String content;
    private int rating;
    private LocalDateTime reviewDate;



    /**
     * Constructs an ExpandedBook with specified details.
     * @param reviewId      the unique identifier of the review
     * @param userId        the unique identifier of the user who made the review
     * @param book          the unique identifier of the book being reviewed
     * @param content       the content of the review
     * @param rating        the rating given in the review
     */
    public Review(String reviewId, User user, Book book, String content, int rating) {
        this.reviewId = reviewId;
        this.user = user;
        this.book = book;
        this.content = content;
        this.rating = rating;
    }

    public String getReviewId() {
        return reviewId;
    }
    public String getUserId() {
        return user.getUserId();
    }
    public Book getBookId() {
        return book;
    }
    public String getContent() {
        return content;
    }
    public int getRating() {
        return rating;
    }
    public LocalDateTime getReviewDate() {
        return reviewDate;
    }
}
