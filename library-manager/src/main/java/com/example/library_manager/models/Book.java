package com.example.library_manager.models;

import java.awt.image.BufferedImage;
import java.sql.Date;
import java.util.List;
import com.example.library_manager.models.Author;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


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
     * Number of ratings the book has received.
     */
    private final int numRatings;

    /**
     * Number of likes for this book.
     */
    private final int heartsCount;

    /**
     * Number of hearts for this book.
     */
    private final boolean isHearted;

    /**
     * Whether the logged-in user has wishlisted this book.
     */
    private final boolean isWishlisted;

    /**
     * Whether the logged-in user has read this book.
     */
    private final boolean isRead;

    /**
     * Whether to show description
     */
    private boolean showdesc;

    /**
     * Constructs a User with specified details.
     *
     * @param isbn          the unique identifier of the book
     * @param title         title of the book
     * @param author        author of the book
     * @param imglink       link to the book cover image
     * @param category      Category of the book
     * @param publishDate   Publish date of the book
     * @param rating        Rating of the book
     * @param numRatings    Number of ratings the book has received
     * @param heartsCount   Number of likes for this book
     * @param isHearted     Whether the logged-in user has liked this book
     * @param isWishlisted  Whether the logged-in user has wishlisted this book
     * @param isRead        Whether the logged-in user has read this book
     * @param showdesc      Whether to show description
     */
    public Book(String isbn, String title, List<Author> authors, String imglink, String category, String publishDate, float rating,
                int numRatings, int heartsCount, boolean isHearted, boolean isWishlisted, boolean isRead)
    {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.imglink = imglink;
        this.category = category;
        this.publishDate = publishDate != null ? publishDate : "N/A";
        this.rating = rating;
        this.numRatings = numRatings;
        this.heartsCount = heartsCount;
        this.isHearted = isHearted;
        this.isWishlisted = isWishlisted;
        this.isRead = isRead;
        this.showdesc = false;
    }


    /**
     * Sets the authors of the book.
     *
     * @param authors List of authors
     */
    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorsString() {
        List<String> authorlist = new ArrayList<String>();
        for(Author a : authors) {
            String name = a.getAuthorName();
            authorlist.add(name);
        }
        String result = String.join(", ", authorlist); 
        return result;
    }
    public List<Author> getAuthors() {
        return this.authors;
    }
    public String getImglink() {
        return imglink;
    }
    public String getCategory() {
        return category;
    }
    
    public String getPublishDate() {
        String input = publishDate;
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy");
        LocalDate date = LocalDate.parse(input, inputFormatter);
        String output = date.format(outputFormatter);
        return output;
    }

    public float getRating() {
        return rating;
    }
    public int getHeartsCount() {
        return heartsCount;
    }
    public boolean isHearted() {
        return isHearted;
    }
    public boolean isWishlisted() {
        return isWishlisted;
    }
    public boolean isRead() {
        return isRead;
    }
    public boolean isShowdesc() {
        return showdesc;
    }
    public int getNumRatings() {
        return numRatings;
    }
}
