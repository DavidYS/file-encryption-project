package com.spring.files.upload.service;

import com.spring.files.upload.model.UserCredentials;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    ResponseEntity<?> signUp(UserCredentials userCredentials);

    Boolean login(UserCredentials userCredentials);

    List<UserCredentials> getUsers();

    String getPrivateSecretKey(String email);
}
