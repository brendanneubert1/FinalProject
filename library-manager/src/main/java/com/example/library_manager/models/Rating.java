
 package com.example.library_manager.models;


/**
 * Represents a review made by a user for a book.
 */

 

    public class Rating {
    private String userId;
    private String bookId;
    private Double rating;

    /**
     * Shows users rating for a book.
     * @param ratingId      the unique identifier of the rating 
     * @param userId        the unique identifier of the user who rated the book
     * @param bookId          the unique identifier of the book being rated
     * @param rating       the rating given to the book
     */


    
    public Rating(String userId, String bookId, Double rating) {
        
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
    }

   
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

}