package com.example.library_manager.models;

import java.awt.image.BufferedImage;
import java.sql.Date;
import java.util.List;

/**
 * Represents a user of the micro blogging platform.
 */
public class Book {

    /**
     * Unique identifier for the user.
     */
    private final String isbn;

    /**
     * Title of the book.
     */
    private final String title;

    /**
     * First name of the user.
     */
    private List<Author> authors;

    /**
     * link to pic of book
     */
    private final String imglink;

    /**
     * Path of the profile image file for the user.
     */
    private final String category;

    /**
     * Publish date of the book.
     */
    private final String publishDate;

    /**
     * Description of the book.
     */

    /**
     * Rating of the book.
     */
    private final float rating;

    /**
     * Whether the logged-in user has liked this book.
     */
    private final boolean liked;

    /**
     * Constructs a User with specified details.
     *
     * @param isbn        the unique identifier of the book
     * @param title       title of the book
     * @param author      author of the book
     * @param imglink    link to the book cover image
     * @param img BufferedImage of the book cover
     * @param category Category of the book
     * @param publishDate Publish date of the book
     * @param rating Rating of the book
     * @param liked Whether the logged-in user has liked this book
     */
    public Book(String isbn, String title, List<Author> authors, String imglink, BufferedImage img, String category, String publishDate, float rating, boolean liked) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.imglink = imglink;
        this.category = category;
        this.publishDate = publishDate;
        this.rating = rating;
        this.liked = liked;
    }

    /**
     * Constructs a User with specified details.
     *
     * @param isbn        the unique identifier of the book
     * @param author      author of the book
     * @param imglink    link to the book cover image
     * @param category Category of the book
     * @param publishDate Publish date of the book
     * @param rating Rating of the book
     */
    public Book(String isbn, String title, List<Author> authors, String imglink, String category, String publishDate, float rating, boolean liked) {

        this(isbn, title, authors, imglink, getImg(isbn), category, publishDate !=null ? publishDate : "N/A", rating, liked);
    }


    /**
     * Sets the authors of the book.
     *
     * @param authors List of authors
     */
    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }



    /**
     * Given an isbn, get img of the book.
     */
    private static BufferedImage getImg(String isbn) {
        // Placeholder for image retrieval logic
        return null;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public List<Author> getAuthors() {
        return authors;
    }
    public String getImglink() {
        return imglink;
    }
    public String getCategory() {
        return category;
    }
    public String getPublishDate() {
        return publishDate;
    }
    public float getRating() {
        return rating;
    }
    public int isLiked() {
        return liked ? 1 : 0;
    }
}
