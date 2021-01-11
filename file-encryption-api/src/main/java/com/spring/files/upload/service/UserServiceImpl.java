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
import java.util.Random;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private CipherHelper cipherHelper = new CipherHelper();

    @SneakyThrows
    @Override
    public boolean signUp(UserCredentials userCredentials) {
        Optional<UserCredentials> userDb = this.userRepository.findByEmail(userCredentials.getEmail());

        if (userDb.isPresent()) {
            return false;
        }

        userCredentials.setPassword(cipherHelper.encrypt(userCredentials.getPassword(), ""));
        userCredentials.setSecretKey(generatePrivateSecretKey());
        userRepository.save(userCredentials);

        return true;

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
        int leftLimit = 41;
        int rightLimit = 122;
        int targetStringLength = 255;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}
