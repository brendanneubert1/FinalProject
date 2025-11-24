package com.example.library_manager.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.library_manager.models.ExpandedBook;
import com.example.library_manager.services.UserService;
import com.example.library_manager.services.BookService;

@Controller
@RequestMapping
public class HomeController {

    private final UserService userService;
    private final BookService bookService;

    @Autowired
    public HomeController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }


    /**
     * This is the specific function that handles the root URL itself.
     * 
     * Note that this accepts a URL parameter called error.
     * The value to this parameter can be shown to the user as an error message.
     * See notes in HashtagSearchController.java regarding URL parameters.
     */
    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) throws SQLException{
        // See notes on ModelAndView in BookmarksController.java.
        ModelAndView mv = new ModelAndView("books_page");

        // Following line populates sample data.
        // You should replace it with actual data from the database.
        String loggedinUser = userService.getLoggedInUser().getUserId();
        //List<ExpandedBook> books = bookService.testExpandedBooks();

        // If an error occured, you can set the following property with the
        // error message to show the error message to the user.
        // An error message can be optionally specified with a url query parameter too.
        String errorMessage = error;
        mv.addObject("errorMessage", errorMessage);

        // Enable the following line if you want to show no content message.
        // Do that if your content list is empty.
        // mv.addObject("isNoContent", true);

        return mv;
    }
    
}
