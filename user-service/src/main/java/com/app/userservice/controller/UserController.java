package com.app.userservice.controller;

import com.app.userservice.entity.User;
import com.app.userservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Internal controller for interservice communication.
 * The measurement-service calls /api/v1/users/resolve via OpenFeign
 * to map a JWT email claim to a userId.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Resolution (Internal)")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/resolve")
    @Operation(summary = "Resolve user ID from email (internal — called by measurement-service)")
    public ResponseEntity<Map<String, Object>> resolveUser(@RequestParam("email") String email) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("role", user.getRole().name());
            log.debug("Resolved user: {} → id={}", email, user.getId());
        } else {
            response.put("id", null);
            response.put("email", email);
            log.warn("User not found for email: {}", email);
        }

        return ResponseEntity.ok(response);
    }
}
