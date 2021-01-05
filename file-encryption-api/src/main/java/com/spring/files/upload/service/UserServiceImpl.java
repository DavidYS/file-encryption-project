package com.spring.files.upload.service;

import com.spring.files.upload.model.UserCredentials;
import com.spring.files.upload.repository.UserRepository;
import com.spring.files.upload.service.helpers.CipherHelper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private CipherHelper cipherHelper = new CipherHelper();

    @SneakyThrows
    @Override
    public ResponseEntity<?> signUp(UserCredentials userCredentials) {
        Optional<UserCredentials> userDb = this.userRepository.findByEmail(userCredentials.getEmail());

        if (userDb.isPresent()) {
            return ResponseEntity.status(409).body("Email already exists");
        }

        userCredentials.setPassword(cipherHelper.encrypt(userCredentials.getPassword(), ""));
        userCredentials.setSecretKey(generatePrivateSecretKey());
        userRepository.save(userCredentials);

        return ResponseEntity.ok().body("User created successfully!");

    }

    @Override
    public Boolean login(UserCredentials userCredentials) {
        Optional<UserCredentials> userDb = this.userRepository.findByEmail(userCredentials.getEmail());

        return userDb.filter(credentials -> cipherHelper.encrypt(userCredentials.getPassword(), "").equals(credentials.getPassword())).isPresent();
    }

    @Override
    public List<UserCredentials> getUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public String getPrivateSecretKey(String email) {
        Optional<UserCredentials> userDb = this.userRepository.findByEmail(email);

        return userDb.map(UserCredentials::getSecretKey).orElse(null);
    }

    private String generatePrivateSecretKey() {
        byte[] array = new byte[256];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }
}
