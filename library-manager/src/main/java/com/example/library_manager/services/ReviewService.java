package com.example.library_manager.services;


import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.library_manager.models.Book;
import com.example.library_manager.models.BrowseResult;
import com.example.library_manager.models.ExpandedBook;
import com.example.library_manager.models.PageLink;

import java.util.List;
import java.util.Map;

import com.example.library_manager.models.Author;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@SessionScope
public class ReviewService {
    final DataSource dataSource;
    @Autowired
    public ReviewService(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public boolean makeReview(String userId, String reviewContent, String bookTitle, int rating, boolean reccommended) {
        // TODO Auto-generated method stub
        // FIRST search for book by title to get bookId (IF TITLE DOESNT MATCH ISBN RETURN FALSE)
        // THEN create review with review object attributes
        return false;
    }

}
