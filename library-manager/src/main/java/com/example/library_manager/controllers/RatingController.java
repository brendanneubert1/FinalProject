package com.example.library_manager.controllers;

import com.example.library_manager.models.User;
import com.example.library_manager.services.RatingService; 
import com.example.library_manager.services.UserService; 
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.*; 

@Controller
public class RatingController {
    private final RatingService ratingService; 
    private final UserService userService; 

    public RatingController(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService; 
        this.userService = userService;
    }

    @PostMapping("/books/{isbn}/rate")
    public String rateBook(
        @PathVariable("isbn") String isbn, 
        @RequestParam("rating") Double ratingValue
    ) {
        User user = userService.getLoggedInUser(); 
        if (user == null) {
            return "redirect:/login"; 
        }

        String userId = user.getUserId(); 
        try {
            ratingService.upsertRating(userId, isbn, ratingValue); 

        } catch (Exception e) {
            e.printStackTrace(); 
        }
        return "redirect:/books/" + isbn; 
    }
}
