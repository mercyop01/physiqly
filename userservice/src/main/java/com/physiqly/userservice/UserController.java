package com.physiqly.userservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public User registerUser(@RequestBody User newUser) {
        // In a real app, you would hash the password here before saving
        User savedUser = userRepository.save(newUser);
        return savedUser;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        // Find the user by email using the new repository method
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        // Check if user exists and if the password matches
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // IMPORTANT: This is a plain text password comparison.
            // In a real application, you MUST use a password hashing library like BCrypt.
            if (user.getPassword().equals(loginRequest.getPassword())) {
                // Passwords match, login successful. Return the user's data.
                return ResponseEntity.ok(user);
            }
        }

        // If user not found or password incorrect, return an error.
        // 401 Unauthorized is the correct status code for a failed login attempt.
        return ResponseEntity.status(401).body("Invalid email or password");
    }
}

