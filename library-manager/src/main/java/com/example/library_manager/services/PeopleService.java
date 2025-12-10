/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/

package com.example.library_manager.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.example.library_manager.models.FollowableUser;
import com.example.library_manager.models.User;


/**
 * This service contains people related functions.
 */
@Service
public class PeopleService {

    // dataSource enables talking to the database.
    private final DataSource dataSource;
    private final UserService userService;
    // passwordEncoder is used for password security.
  
    // This holds 
    private User loggedInUser = null;

    public PeopleService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    
    /**
     * This function should query and return all users that 
     * are followable. The list should not contain the user 
     * with id userIdToExclude.
     */
    public List<FollowableUser> getFollowableUsers(String userIdToExclude) {
       

           List<FollowableUser> followableUserList = new ArrayList<>();
        // Write an SQL query to find the users that are not the current user.
        final String sqlString = "select * from `user` where user.userId != " + userIdToExclude;

        // Run the query with a datasource.
        // See UserService.java to see how to inject DataSource instance and
        // use it to run a query.

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sqlString)) {

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String currUserID = rs.getString("userId");
                    String currRowFirstName = rs.getString("firstName");
                    String currRowLastName = rs.getString("lastName");
                    boolean isFollowed = getIsFollowed(userIdToExclude, currUserID);
                    String lastPostedDate = findLastPostedDate(currUserID);
                   
                    FollowableUser currFollowableUser = new FollowableUser(currUserID, currRowFirstName, currRowLastName, isFollowed, lastPostedDate);
                    followableUserList.add(currFollowableUser);
                } // while

            }

        } catch (SQLException sqlex) {
            System.err.println("SQL EXCEPTION " + sqlex.getMessage());
        } // try

        // Replace the following line and return the list you created.
        return followableUserList;
    }

     public List<User> getAllUsers() throws SQLException {
        final String sql = "SELECT * FROM user";
        List<User> Users = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("userId");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");

                    Users.add(new User(username, firstName, lastName));
                }
            }
        }

        return Users;
    }

    private String findLastPostedDate(String userId) {
        
        final String sqlString = "SELECT max(created_date) AS latest_post FROM review WHERE userId = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sqlString)) {


            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    System.out.println("DATE/USERID: " + userId);
                    String dbDateTime = rs.getString("latest_post");
                    if (dbDateTime == null) {
                        return "N/A";
                    } else {
                        LocalDateTime post_dateTime = LocalDateTime.parse(dbDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String formatted_post_date = post_dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a"));
                        return formatted_post_date;                   
                    }
                } 
                return "N/A";
            }
        } catch (SQLException sqle) {
            System.err.println("SQL EXCEPTION " + sqle.getMessage());
            return "N/A";
        } // try
    } 

    private boolean getIsFollowed(String currUserId, String followUserId) {
        final String sqlString = "select * from follows where userId = ? and follows = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sqlString)) {

            pstmt.setString(1, currUserId);
            pstmt.setString(2, followUserId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    return true;
                } // while
                return false;
            }
        } catch (SQLException sqlex) {
            System.err.println("SQL EXCEPTION: " + sqlex.getMessage());
            return false;
        } // try
    } 

    

    public boolean followRequest(String userId, boolean isFollow) {

        String currentUserId = userService.getLoggedInUser().getUserId();

        if (isFollow) {
            // following the user

            String sqlString = "insert into follows (userId, follows) values (?, ?)";


            try (Connection conn = dataSource.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sqlString)) {

                pstmt.setString(1, currentUserId);
                pstmt.setString(2, userId);

                int rowsAffected = pstmt.executeUpdate();
                    
                
                // The  URL assigns 1 to userId and false to isFollow if failed; applies if finds rows
                if (rowsAffected > 0) {
                    return true;
                } else {
                    return false;
                }

            } catch (SQLException sqle) {
                System.err.println("SQL EXCEPTION " + sqle.getMessage());
                return false;
            } // try

        } else {
            // for unfollowing the user

            String sqlString = "delete from follows where userId = ? and follows = ?";

            try (Connection conn = dataSource.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sqlString)) {

                pstmt.setString(1, currentUserId);
                pstmt.setString(2, userId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    return true;
                } else {
                    return false;
                }

            } catch (SQLException sqle) {
                System.err.println("SQL EXCEPTION " + sqle.getMessage());
                return false;
            } // try

        } // if

    } 

}



