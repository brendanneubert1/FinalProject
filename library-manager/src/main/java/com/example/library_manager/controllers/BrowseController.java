package com.example.library_manager.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.library_manager.services.UserService;
import com.example.library_manager.services.BookService;
import com.example.library_manager.models.Book;
import com.example.library_manager.models.ExpandedBook;
import com.example.library_manager.models.BrowseResult; 



@Controller
@RequestMapping("/browse")
public class BrowseController {
    private final BookService bookService;
    private final UserService userService;


    @Autowired
    public BrowseController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }



    @GetMapping("")
    public ModelAndView webpage(
            @RequestParam(name="page", required=false) Integer page,
            @RequestParam(name="size", required=false) Integer size,
            @RequestParam(name="title",     required=false) String title,
            @RequestParam(name="author",    required=false) String author,
            @RequestParam(name="category",  required=false) String category,
            @RequestParam(name="minRating", required=false) Float minRating
    ) throws SQLException {


         if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        if (title == null) {
            title = "title";
        }
        if (author == null) {
            author = "name";
        }
        if (category == null) {
            category = "category";
        }
        if (minRating == null) {
            minRating = 0.0f;
        }

        
        BrowseResult result = bookService.getBrowsePage(page,size,title, author, category, minRating);

        ModelAndView mv = new ModelAndView("browse_page");
        mv.addObject("books", result.getBooks());
        mv.addObject("page", result.getPage());
        mv.addObject("size", result.getSize());
        mv.addObject("totalPages", result.getTotalPages());
        mv.addObject("hasPrev", result.isHasPrev());
        mv.addObject("hasNext", result.isHasNext());
        mv.addObject("prevPage", result.getPrevPage());
        mv.addObject("nextPage", result.getNextPage());
        mv.addObject("pages", result.getPages());

        // For filter
        mv.addObject("title", title);
        mv.addObject("author", author);
        mv.addObject("category", category);
        mv.addObject("minRating", minRating);

        // size flags for the dropdown
        mv.addObject("sizeIs10", size == 10);
        mv.addObject("sizeIs20", size == 20);
        mv.addObject("sizeIs50", size == 50);

        return mv;
    }





    
}
