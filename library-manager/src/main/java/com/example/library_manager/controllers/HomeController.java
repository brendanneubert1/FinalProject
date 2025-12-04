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

import com.example.library_manager.models.Book;
import com.example.library_manager.services.UserService;
import com.example.library_manager.services.BookService;
import com.example.library_manager.services.ReviewService;

@Controller
@RequestMapping
public class HomeController {

    private final UserService userService;
    private final BookService bookService;
    private final ReviewService reviewService;

    @Autowired
    public HomeController(UserService userService,
                          BookService bookService,
                          ReviewService reviewService) {
        this.userService = userService;
        this.bookService = bookService;
        this.reviewService = reviewService;
    }

    /**
     * Root URL – home_page.
     * Also passes state for the new_review_form fragment.
     */
    @GetMapping
    public ModelAndView webpage(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "bookId", required = false) String bookId,
            @RequestParam(name = "bookTitle", required = false) String bookTitle
    ) throws SQLException {

        ModelAndView mv = new ModelAndView("home_page");

        String loggedinUser = userService.getLoggedInUser().getUserId();

        mv.addObject("errorMessage", error);

        //new_review_form fragment
        mv.addObject("selectedBookId", bookId != null ? bookId : "");
        mv.addObject("bookTitle", bookTitle != null ? bookTitle : "");
        mv.addObject("error", error);        // used by {{#error}} in the fragment
        mv.addObject("searchResults", null); // no results on plain GET

        return mv;
    }

    /**
     * Handles the search button in the new_review_form fragment.
     * Renders home_page again, but with searchResults populated.
     */
    @PostMapping("/createreview/search")
    public ModelAndView searchBooksForReview(
            @RequestParam("bookTitle") String bookTitle
    ) throws SQLException {

        ModelAndView mv = new ModelAndView("home_page");

        String loggedinUser = userService.getLoggedInUser().getUserId();
        // use loggedinUser in home_page if needed

        List<Book> results = bookService.searchBooksByTitle(bookTitle);

        mv.addObject("errorMessage", null);
        mv.addObject("error", null);

        // === For the new_review_form fragment ===
        mv.addObject("bookTitle", bookTitle);
        mv.addObject("selectedBookId", "");
        mv.addObject("searchResults", results);

        return mv;
    }

    /**
     * Handles the actual review submission.
     * If no bookId, redirects back to home_page with "Please select a book first".
     */
    @PostMapping("/createreview")
    public String submitReview(
            @RequestParam(value = "bookId", required = false) String bookId,
            @RequestParam("rating") int rating,
            @RequestParam("recommended") boolean recommended,
            @RequestParam("reviewContent") String reviewContent,
            @RequestParam("bookTitle") String bookTitle
    ) throws SQLException {

        // If no bookId, redirect back with error
        if (bookId == null || bookId.isEmpty()) {
            String encodedTitle = bookTitle != null
                    ? URLEncoder.encode(bookTitle, StandardCharsets.UTF_8)
                    : "";
            String encodedError = URLEncoder.encode("Please select a book first",
                    StandardCharsets.UTF_8);

            // Redirect back to "/" (home_page) with error and preserved title
            return "redirect:/?error=" + encodedError +
                   "&bookTitle=" + encodedTitle;
        }

        System.out.println("User is submitting a review for bookId: " + bookId);
        System.out.println("Rating: " + rating);
        System.out.println("Recommended: " + recommended);
        System.out.println("Review Content: " + reviewContent);
        String userId = userService.getLoggedInUser().getUserId();

        boolean success = reviewService.makeReview(userId, bookId, reviewContent, rating, recommended);
        
        if (!success) {
            String message = URLEncoder.encode("Failed to submit review. Please try again.",
                    StandardCharsets.UTF_8);
            return "redirect:/?error=" + message;
        }
        // After successful submit
        return "redirect:/";
    }
}
