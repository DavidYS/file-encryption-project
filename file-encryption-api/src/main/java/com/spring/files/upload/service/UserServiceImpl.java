package com.spring.files.upload.service;

import com.spring.files.upload.model.UserCredentials;
import com.spring.files.upload.repository.UserRepository;
import com.spring.files.upload.service.helpers.CipherHelper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private CipherHelper cipherHelper = new CipherHelper();

    @SneakyThrows
    @Override
    public UserCredentials signUp(UserCredentials userCredentials) {
        userCredentials.setPassword(cipherHelper.encrypt(userCredentials.getPassword()));
        return userRepository.save(userCredentials);
    }

    @Override
    public Boolean login(UserCredentials userCredentials) {
        Optional<UserCredentials> userDb = this.userRepository.findByEmail(userCredentials.getEmail());

        return userDb.filter(credentials -> cipherHelper.encrypt(userCredentials.getPassword()).equals(credentials.getPassword())).isPresent();
    }

    @Override
    public List<UserCredentials> getUsers() {
        return this.userRepository.findAll();
    }
}
