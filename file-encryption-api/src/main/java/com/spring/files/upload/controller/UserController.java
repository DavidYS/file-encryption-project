package com.spring.files.upload.controller;

import com.spring.files.upload.model.UserCredentials;
import com.spring.files.upload.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserCredentials> signUp(@RequestBody UserCredentials userCredentials){
        return ResponseEntity.ok().body(this.userService.signUp(userCredentials));
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody UserCredentials userCredentials){
        return ResponseEntity.ok().body(this.userService.login(userCredentials));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserCredentials>> getUsers() {
        return ResponseEntity.ok().body(this.userService.getUsers());
    }
}
