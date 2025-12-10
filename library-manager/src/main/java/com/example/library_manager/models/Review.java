package com.example.library_manager.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Represents a review made by a user for a book.
 */



public class Review {
    private String reviewId;
    private User user;
    private Book book;
    private String content;
    private boolean recommended;
    private LocalDateTime reviewDate;

    // Constructor
    public Review(String reviewId, User user, Book book, String content, boolean recommended, LocalDateTime reviewDate) {
        this.reviewId = reviewId;
        this.user = user;
        this.book = book;
        this.content = content;
        this.recommended = recommended;
        this.reviewDate = reviewDate;
    }

    // Getters
    public String getReviewId() {
        return reviewId;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public String getContent() {
        return content;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public String getReviewDate() {
        LocalDateTime dt = LocalDateTime.parse("2025-12-10T00:15:42");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a");
        String formatted = dt.format(formatter);
        return formatted;
    }

   
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public void setUserId(String userId) {
        this.user.setUserId(userId);
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
}