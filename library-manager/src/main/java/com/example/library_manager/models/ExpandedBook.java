package com.example.library_manager.models;

import java.util.List;

public class ExpandedBook extends Book {

    private final String description;
    private final int numPages;

    // New fields for ratings
    private Double avgRating;
    private Double userRatingValue;

    /**
     * Constructs an ExpandedBook with specified details.
     *
     * @param isbn          the unique identifier of the book
     * @param title         title of the book
     * @param authors       list of authors
     * @param imglink       link to image of book
     * @param category      category of the book
     * @param publishDate   publish date of the book
     * @param rating        average rating of the book
     * @param numRatings    number of ratings
     * @param heartsCount   number of likes
     * @param isHearted     whether logged-in user liked this book
     * @param isWishlisted  whether logged-in user wishlisted this book
     * @param isRead        whether logged-in user read this book
     * @param numPages      number of pages
     * @param description   description of the book
     */
    public ExpandedBook(String isbn, String title, List<Author> authors, String imglink, String category, String publishDate,
                        float rating, int numRatings, int heartsCount, boolean isHearted, boolean isWishlisted,
                        boolean isRead, int numPages, String description) {

        super(isbn, title, authors, imglink, category, publishDate, rating,
                numRatings, heartsCount, isHearted, isWishlisted, isRead);

        this.description = description;
        this.numPages = numPages;
    }

    // --- Getters ---
    public String getDescription() {
        return description;
    }

    public int getNumPages() {
        return numPages;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public Double getUserRatingValue() {
        return userRatingValue;
    }

    // --- Setters for the new rating fields ---
    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public void setUserRatingValue(Double userRatingValue) {
        this.userRatingValue = userRatingValue;
    }
}