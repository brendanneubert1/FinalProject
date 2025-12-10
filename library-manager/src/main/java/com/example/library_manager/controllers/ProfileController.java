package com.example.library_manager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.library_manager.models.Book;
import com.example.library_manager.models.ExpandedBook;
import com.example.library_manager.services.UserService;
import com.example.library_manager.services.BookService;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.library_manager.models.Review;
import com.example.library_manager.services.ReviewService;

import java.sql.SQLException;




@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final BookService bookService;
    private final ReviewService reviewService;
    
    @Autowired
    public ProfileController(UserService userService, BookService bookService, ReviewService reviewService) {
        this.userService = userService;
        this.bookService = bookService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public ModelAndView profileOfLoggedInUser() throws SQLException{
        System.out.println("User is attempting to view profile of the logged in user.");
        return profileOfSpecificUser(userService.getLoggedInUser().getUserId());
    }
    
    @GetMapping("/{userId}")
    public ModelAndView profileOfSpecificUser(@PathVariable("userId") String userId) throws SQLException{
        System.out.println("User is attempting to view profile: " + userId);
        
        // See notes on ModelAndView in BookmarksController.java.
        ModelAndView mv = new ModelAndView("profile_page");

        // Following line populates sample data.
        // You should replace it with actual data from the database.
        List<ExpandedBook> wishlistedBooks = bookService.getWishlistedBooks(userId);
        List<ExpandedBook> readBooks = bookService.getReadBooks(userId);
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        //List<Post> posts = Utility.createSamplePostsListWithoutComments();
        mv.addObject("wishlistedbooks", wishlistedBooks);
        mv.addObject("readbooks", readBooks);
        mv.addObject("reviews", reviews);


        // If an error occured, you can set the following property with the
        // error message to show the error message to the user.
        // String errorMessage = "Some error occured!";
        // mv.addObject("errorMessage", errorMessage);

        // Enable the following line if you want to show no content message.
        // Do that if your content list is empty.
        // mv.addObject("isNoContent", true);
        
        return mv;
    }

}
