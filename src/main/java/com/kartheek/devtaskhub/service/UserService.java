package com.kartheek.devtaskhub.service;

import com.kartheek.devtaskhub.model.User;
import com.kartheek.devtaskhub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getUsers() {
        return userRepo.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public User updateUser(Long id, User newUser) {
        return userRepo.findById(id).map(user -> {
            user.setUsername(newUser.getUsername());
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());
            user.setRole(newUser.getRole());
            return userRepo.save(user);
        }).orElseThrow(() -> new RuntimeException("User doesn't exists"));
    }

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}
