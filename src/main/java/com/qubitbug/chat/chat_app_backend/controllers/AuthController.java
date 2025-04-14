package com.qubitbug.chat.chat_app_backend.controllers;

import com.qubitbug.chat.chat_app_backend.entities.UserCredentials;
import com.qubitbug.chat.chat_app_backend.entities.UserEntity;
import com.qubitbug.chat.chat_app_backend.services.UserService;
import com.qubitbug.chat.chat_app_backend.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> loginPost(@RequestBody UserCredentials userCredentials) {
        if (userCredentials.getEmail() == null || userCredentials.getPassword() == null) {
            return new ResponseEntity<>(Map.of("status", false, "message", "Email or password is required"), HttpStatus.BAD_REQUEST);
        }
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCredentials.getEmail(), userCredentials.getPassword()));
            final UserEntity userEntity = userService.findByEmail(userCredentials.getEmail());


            if(userEntity == null) {
                return new ResponseEntity<>(Map.of("status", false, "message", "Wrong Email or Password "), HttpStatus.NOT_FOUND);
            }else{
                String jwtToken = jwtUtil.generateToken(userEntity.getEmail());
                return new ResponseEntity<>(Map.of("status", true, "data",Map.of(
                        "token", jwtToken,
                        "user", userEntity
                )), HttpStatus.OK);
            }
        }catch (AuthenticationException e){
            log.error("Exception occurred while createAuthenticationToken", e);
            return new ResponseEntity<>(Map.of(
                    "status", false,
                    "message", "Invalid email or password"
            ), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPost(@RequestBody UserEntity userEntity) {
        List<String> errors = new ArrayList<>();
        if(userEntity.getEmail().isEmpty()) errors.add("email is required");
        if(userEntity.getPassword().isEmpty()) errors.add("password is required");
        if(userEntity.getUsername().isEmpty()) errors.add("username is required");
        if(!errors.isEmpty()) return new ResponseEntity<>(Map.of("status", false, "message", errors), HttpStatus.BAD_REQUEST);
        log.info(userEntity.toString());
        try{
            if(userService.checkEmailExists(userEntity.getEmail())) {
                return new ResponseEntity<>(
                        Map.of("status", false, "message", "Email already exists"),
                        HttpStatus.BAD_REQUEST
                );
            }
            userService.register(userEntity);
            return new ResponseEntity<>(Map.of("status", true, "data", userEntity), HttpStatus.CREATED);
        }catch (Exception e){
            log.error("Exception occurred while creating user", e);
            return new ResponseEntity<>(
                    Map.of("status", false, "message", "Error occurred while creating user"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}

