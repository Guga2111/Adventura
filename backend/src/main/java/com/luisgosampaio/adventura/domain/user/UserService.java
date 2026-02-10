package com.luisgosampaio.adventura.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional(readOnly = true)
    public User getUser (Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id: " + id + " does not exists")); // UserNotFoundException
    }

    @Transactional(readOnly = true)
    public User getUser (String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email: " + email + " does not exists")); // UserNotFoundException
    }

    @Transactional
    public User saveUser (User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("This email: " + user.getEmail() + " already exists");
        }

        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
}
