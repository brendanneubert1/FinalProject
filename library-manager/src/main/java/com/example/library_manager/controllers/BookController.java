/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package com.example.library_manager.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.library_manager.models.ExpandedBook;
import com.example.library_manager.services.BookService;
import com.example.library_manager.services.UserService;


/**
 * Handles /post URL and its sub urls.
 */
@Controller
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;
    private final UserService userService;



    @Autowired
    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
        
    }

    /**
     * This function handles the /post/{postId} URL.
     * This handlers serves the web page for a specific post.
     * Note there is a path variable {postId}.
     * An example URL handled by this function looks like below:
     * http://localhost:8081/post/1
     * The above URL assigns 1 to postId.
     * 
     * See notes from HomeController.java regardig error URL parameter.
     */

    @GetMapping("/{bookId}")
    public ModelAndView webpage(@PathVariable("bookId") String bookId,
            @RequestParam(name = "error", required = false) String error) throws SQLException {
        System.out.println("The user is attempting to view book with id: " + bookId);
        // See notes on ModelAndView in BookmarksController.java.
        ModelAndView mv = new ModelAndView("books_page");

        String userId = userService.getLoggedInUser().getUserId();

        // Fetch the book with likes, wishlist, read info
        ExpandedBook book = bookService.getBooksByIsbn(bookId, userId);

         mv.addObject("books", book == null ? new ArrayList<>() : List.of(book));
        mv.addObject("errorMessage", error);

    
        // Enable the following line if you want to show no content message.
        // Do that if your content list is empty.
        // mv.addObject("isNoContent", true);

        return mv;
    }

    /**
     * Handles comments added on posts.
     * See comments on webpage function to see how path variables work here.
     * This function handles form posts.
     * See comments in HomeController.java regarding form submissions.
     */
    
    /**
     * Handles likes added on posts.
     * See comments on webpage function to see how path variables work here.
     * See comments in PeopleController.java in followUnfollowUser function regarding 
     * get type form submissions and how path variables work.
     */
    @GetMapping("/{bookId}/like/{isAdd}")
    public String addOrRemoveHeart(@PathVariable("bookId") String bookId,
                                @PathVariable("isAdd") Boolean isAdd) throws SQLException {
        System.out.println("The user is attempting to add or remove a heart for a book:");
        System.out.println("\tbookId: " + bookId);
        System.out.println("\tisAdd: " + isAdd);

        String userId = userService.getLoggedInUser().getUserId();
        // Call the likeBook method with the isAdd flag
        boolean success = bookService.likeBook(userId, bookId, isAdd);

        if (success) {
            return "redirect:/book/" + bookId;
        } else {
            String message = URLEncoder.encode("Failed to (un)like the book. Please try again.",
                                            StandardCharsets.UTF_8);
            return "redirect:/book/" + bookId + "?error=" + message;
        }
    }



    /**
     * Handles bookmarking posts.
     * See comments on webpage function to see how path variables work here.
     * See comments in PeopleController.java in followUnfollowUser function regarding 
     * get type form submissions.
     */
    @GetMapping("/{bookId}/wishlist/{isAdd}")
    public String addOrRemoveWishlist(@PathVariable("bookId") String bookId,
            @PathVariable("isAdd") Boolean isAdd) throws SQLException{
        System.out.println("The user is attempting add or remove a bookmark:");
        System.out.println("\tbookId: " + bookId);
        System.out.println("\tisAdd: " + isAdd);

        // Redirect the user if the comment adding is a success.
        // return "redirect:/post/" + postId;
         boolean success = bookService.wishlistBook(userService.getLoggedInUser().getUserId(), bookId);
        if (success) {
            return "redirect:/book/" + bookId;
        }

        // Redirect the user with an error message if there was an error.
        String message = URLEncoder.encode("Failed to (un)bookmark the book. Please try again.",
                StandardCharsets.UTF_8);
        return "redirect:/book/" + bookId + "?error=" + message;
    }

    /**
     * Handles marking books as read.
     * See comments on webpage function to see how path variables work here.
     * See comments in PeopleController.java in followUnfollowUser function regarding 
     * get type form submissions.
     */
    @GetMapping("/{bookId}/read/{isAdd}")
    public String markOrUnmarkRead(@PathVariable("bookId") String bookId,
            @PathVariable("isAdd") Boolean isAdd) throws SQLException{
        System.out.println("The user is attempting mark or unmark a book as read:");
        System.out.println("\tbookId: " + bookId);
        System.out.println("\tisAdd: " + isAdd);

        // Redirect the user if the comment adding is a success.
        // return "redirect:/post/" + postId;
        boolean success = bookService.markBookAsRead(userService.getLoggedInUser().getUserId(), bookId);
        if (success) {
            return "redirect:/book/" + bookId;
        }

        // Redirect the user with an error message if there was an error.
        String message = URLEncoder.encode("Failed to (un)mark the book as read. Please try again.",
                StandardCharsets.UTF_8);
        return "redirect:/book/" + bookId + "?error=" + message;
    }

}
