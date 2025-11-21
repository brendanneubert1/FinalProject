package com.example.library_manager.models;

import java.awt.image.BufferedImage;
import java.sql.Date;
import java.util.List;


public class ExpandedBook extends Book {
    /**
     * Number of times the book has been wishlisted.
     */
    private final String description;
    private final boolean liked;
    private final boolean wishlisted;
    private final int numRatings;
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
     * @param liked         whether the logged-in user has liked this book
     * @param wishlisted    number of times the book has been wishlisted
     * @param numRatings    number of ratings the book has received
     * @param numPages     number of pages in the book
     */
    public ExpandedBook(String isbn, String title, List<Author> authors, String imglink,
            String category, String publishDate, String description,
            float rating, boolean liked, boolean wishlisted, int numRatings, int numPages) {
        super(isbn, title, authors, imglink, category, publishDate != null ? publishDate : "N/A", rating, liked);
        this.description = description;
        this.liked = liked;
        this.wishlisted = wishlisted;
        this.numRatings = numRatings;
        this.numPages = numPages;

    }

    /**
     * Returns the number of times the book has been wishlisted.
     *
     * @return the wishlist count
     */
    public int isWishlisted() {
        return wishlisted ? 1 : 0;
    }

    /**
     * Returns the description of the book.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    public int isLiked() {
        return liked ? 1 : 0;
    }
}
