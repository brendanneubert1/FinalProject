package com.example.library_manager.models;

import java.awt.image.BufferedImage;
import java.sql.Date;


/**
 * Represents a user of the micro blogging platform.
 */
public class Author {

    /**
     * Unique identifier for the author.
     */
    private final int authorId;

    /**
     * name of the author.
     */
    private final String authorname;

 

    /**
     * Constructs a User with specified details.
     *
     * @param authorId        the unique identifier of the author
     * @param authorname      author name
     */
    public Author(int authorId, String authorname) {
        this.authorId = authorId;
        this.authorname = authorname;

    }

    /**
     * Given an isbn, get img of the book.
     */

    private String getAuthorname() {
        // Placeholder for image retrieval logic
        return this.authorname;
    }
}
