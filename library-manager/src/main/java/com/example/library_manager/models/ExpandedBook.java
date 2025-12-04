package com.example.library_manager.models;

import java.awt.image.BufferedImage;
import java.sql.Date;
import java.util.List;


public class ExpandedBook extends Book {
    /**
     * Number of times the book has been wishlisted.
     */
    private final String description;
    private final int numPages;


    /**
     * Constructs an ExpandedBook with specified details.
     *
     * @param isbn          the unique identifier of the book
     * @param author        author of the book
     * @param imglink       link to pic of book
     * @param img           image of the book
     * @param category      category of the book
     * @param publishDate   publish date of the book
     * @param description   description of the book
     * @param rating        rating of the book
     * @param numRatings    number of ratings the book has received
     * @param likesCount    Number of likes for this book
     * @param heartsCount   Number of likes for this book
     * @param isHearted     Whether the logged-in user has liked this book
     * @param isWishlisted  Whether the logged-in user has wishlisted this book
     * @param isRead        Whether the logged-in user has read this book
     * @param showdesc     Whether to show description
     * @param wishlisted    number of times the book has been wishlisted
     * @param numRatings    number of ratings the book has received
     * @param numPages     number of pages in the book
     */
    public ExpandedBook(String isbn, String title, List<Author> authors, String imglink, String category, String publishDate, float rating,
                        int numRatings, int heartsCount, boolean isHearted, boolean isWishlisted, boolean isRead, int numPages, String description) {
        
        super(isbn, title, authors, imglink, category, publishDate, rating,
                numRatings, heartsCount, isHearted, isWishlisted, isRead);
        
        this.description = description;
        this.numPages = numPages;

    }

    /**
     * Returns the number of times the book has been wishlisted.
     *
     * @return the wishlist count
     */

    /**
     * Returns the description of the book.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    public int getNumPages() {
    return numPages;
}
}
