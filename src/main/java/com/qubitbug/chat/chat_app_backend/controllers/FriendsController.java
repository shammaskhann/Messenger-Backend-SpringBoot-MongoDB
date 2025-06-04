package com.qubitbug.chat.chat_app_backend.controllers;


import com.qubitbug.chat.chat_app_backend.entities.FriendEntity;
import com.qubitbug.chat.chat_app_backend.entities.SearchedFriendEntity;
import com.qubitbug.chat.chat_app_backend.entities.UserEntity;
import com.qubitbug.chat.chat_app_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/friend")
@RestController
public class FriendsController{

  @Autowired
  UserService userService;

  @GetMapping("/get-friends")
    ResponseEntity<?> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
       try{
         UserEntity user = userService.findByEmail(userDetails.getUsername());
           System.out.println(user.getFriends());
         if (user == null) {
           return ResponseEntity.badRequest().body(Map.of("status", false, "message", "User not found"));
         }
         List<FriendEntity> friends = user.getFriends();
         return ResponseEntity.ok(Map.of("status", true, "friends", friends));
       } catch (Exception e) {
         return ResponseEntity.internalServerError().body(Map.of("status", false, "message", e.getMessage()));

       }
    }

    @GetMapping("/get-all-users")
    ResponseEntity<?> getAllUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String search) {

        try {
            UserEntity user = userService.findByEmail(userDetails.getUsername());
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("status", false, "message", "User not found"));
            }

            // Get all users except current user
            List<UserEntity> allUsersFetched = userService.getAllUsers()
                    .stream()
                    .filter(u -> !u.getId().equals(user.getId()))
                    .collect(Collectors.toList());

            // Get friend IDs and blocked user IDs for filtering
            Set<String> friendIds = user.getFriends().stream()
                    .map(FriendEntity::getId)
                    .collect(Collectors.toSet());

            Set<String> blockedUserIds = new HashSet<>(user.getBlockedUsersId());

            // Filter out friends and blocked users
            List<UserEntity> filteredUsers = allUsersFetched.stream()
                    .filter(u -> !friendIds.contains(u.getId()) && !blockedUserIds.contains(u.getId()))
                    .collect(Collectors.toList());

            // Apply search filter if parameter is provided
            if (search != null && !search.isEmpty()) {
                String searchLower = search.toLowerCase();
                filteredUsers = filteredUsers.stream()
                        .filter(u -> u.getUsername().toLowerCase().contains(searchLower))
                        .collect(Collectors.toList());
            }

            // Map to SearchedFriendEntity with mutual friends
            List<SearchedFriendEntity> result = filteredUsers.stream()
                    .map(u -> {
                        // Find mutual friends
                        List<FriendEntity> mutualFriends = user.getFriends().stream()
                                .filter(f -> u.getFriends().stream()
                                        .anyMatch(uf -> uf.getId().equals(f.getId())))
                                .collect(Collectors.toList());

                        return new SearchedFriendEntity(
                                u.getId(),
                                u.getUsername(),
                                u.getEmail(),
                                u.getDeviceToken(),
                                mutualFriends
                        );
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of("status", true, "users", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", false, "message", e.getMessage()));
        }
    }
}
