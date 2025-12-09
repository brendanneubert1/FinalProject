package com.example.library_manager.models;

import java.time.LocalDateTime;


/**
 * Represents a review made by a user for a book.
 */



public class Review {
    private String reviewId;
    private String userId;
    private Book book;
    private String content;
    private boolean recommended;
    private LocalDateTime reviewDate;

    // Constructor
    public Review(String reviewId, String userId, Book book, String content, boolean recommended, LocalDateTime reviewDate) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.book = book;
        this.content = content;
        this.recommended = recommended;
        this.reviewDate = reviewDate;
    }

    // Getters
    public String getReviewId() {
        return reviewId;
    }

    public String getUserId() {
        return userId;
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

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

   
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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