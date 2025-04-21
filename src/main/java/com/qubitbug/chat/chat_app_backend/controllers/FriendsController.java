package com.qubitbug.chat.chat_app_backend.controllers;


import com.qubitbug.chat.chat_app_backend.entities.FriendEntity;
import com.qubitbug.chat.chat_app_backend.entities.UserEntity;
import com.qubitbug.chat.chat_app_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
         if (user == null) {
           return ResponseEntity.badRequest().body(Map.of("status", false, "message", "User not found"));
         }
         List<FriendEntity> friends = user.getFriends();
         return ResponseEntity.ok(Map.of("status", true, "friends", friends));
       } catch (Exception e) {
         return ResponseEntity.internalServerError().body(Map.of("status", false, "message", e.getMessage()));

       }
    }
}
