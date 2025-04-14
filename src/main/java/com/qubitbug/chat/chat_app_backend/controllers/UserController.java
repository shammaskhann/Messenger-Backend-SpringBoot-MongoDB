package com.qubitbug.chat.chat_app_backend.controllers;

import com.qubitbug.chat.chat_app_backend.entities.FriendRequest;
import com.qubitbug.chat.chat_app_backend.entities.UserEntity;
import com.qubitbug.chat.chat_app_backend.services.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.ServerException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @GetMapping("/send-friend-requests")
    ResponseEntity<?> sendFriendRequest(@AuthenticationPrincipal UserDetails sender, @RequestParam String receiverId) {
        UserEntity senderUser = userService.findByEmail(sender.getUsername());
        if (receiverId == null) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Receiver ID is required"));
        }
        if (senderUser == null) {
            return new ResponseEntity<>(Map.of("status", false, "message", "Sender not found"), HttpStatus.UNAUTHORIZED);
        }
        boolean isSended = false;
        try{
            isSended = userService.sendFriendRequest(senderUser.getId(), receiverId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", e.getMessage()));
        }
        try{
            if(isSended){
                return ResponseEntity.ok(Map.of("status", true, "message", "Friend request sent successfully"));
            }
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Failed to send friend request"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Failed to send friend request","error", e.getMessage()));
        }
    }

    @GetMapping("/cancel-friend-request")
    ResponseEntity<?> cancelFriendRequest(@RequestParam String senderId, @AuthenticationPrincipal UserDetails receiver) {
        UserEntity receiverUser = userService.findByEmail(receiver.getUsername());
        if (senderId == null) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Sender ID is required"));
        }
        if (receiverUser == null) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Receiver not found"));
        }
        try{
            boolean isCanceled = userService.cancelFriendRequest(senderId, receiverUser.getId());
            return ResponseEntity.ok(Map.of("status", true, "message", "Friend request canceled successfully"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Failed to cancel friend request"));
        }
    }

    @GetMapping("/accept-friend-request")
    ResponseEntity<?> acceptFriendRequest(@RequestParam String senderId, @AuthenticationPrincipal UserDetails receiver) {
        UserEntity receiverUser = userService.findByEmail(receiver.getUsername());
        if (senderId == null) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Sender ID is required"));
        }
        if (receiverUser == null) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Receiver not found"));
        }
        try{
            boolean isAccepted = userService.acceptFriendRequest(senderId, receiverUser.getId());
            if (!isAccepted) {
                return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Friend request not found"));
            }
            return ResponseEntity.ok(Map.of("status", true, "message", "Friend request accepted successfully"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Failed to accept friend request"));
        }
    }

    @GetMapping("/friend-requests")
    ResponseEntity<?> getFriendRequest(@AuthenticationPrincipal UserDetails user) {
        UserEntity userEntity = userService.findByEmail(user.getUsername());
        if (userEntity == null) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "User not found"));
        }
        List<FriendRequest> friendRequests = userService.getFriendRequests(userEntity.getId());
        return ResponseEntity.ok(Map.of("status", true, "friendRequests", friendRequests));

    }
}
