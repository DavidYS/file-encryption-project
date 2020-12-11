package com.spring.files.upload.service;

import com.spring.files.upload.model.UserCredentials;

import java.util.List;

public interface UserService {

    UserCredentials signUp(UserCredentials userCredentials);

    Boolean login(UserCredentials userCredentials);

    List<UserCredentials> getUsers();
}
