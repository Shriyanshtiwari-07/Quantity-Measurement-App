package com.app.userservice.controller;

import lombok.extern.slf4j.Slf4j;
import com.app.userservice.dto.request.*;
import com.app.userservice.dto.response.*;
import com.app.userservice.entity.User;
import com.app.userservice.repository.UserRepository;
import com.app.userservice.security.UserPrincipal;
import com.app.userservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * AuthController
 *
 * Handles registration, login, OTP, password management.
 *
 * In the microservices architecture (Option A — gateway-only auth):
 * - /login, /register, /otp, /forgotPassword are public and work directly.
 * - /me and /resetPassword require an authenticated user.
 *   The gateway validates the JWT and injects X-User-Email header.
 *   This controller checks both the SecurityContext (for direct access / OAuth2 flows)
 *   and the X-User-Email header (for gateway-routed requests).
 */
@Slf4j @Validated @RestController @RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController {

    private final AuthenticationService authService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * GET /me — returns the current user's profile.
     * Works via:
     *   1. SecurityContext (direct access with JWT, or OAuth2 flow)
     *   2. X-User-Email header (gateway-routed requests where gateway stripped the JWT)
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(
            Authentication authentication,
            @RequestHeader(value = "X-User-Email", required = false) String gatewayEmail) {

        // Try SecurityContext first (direct access / OAuth2)
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal p) {
            User u = p.getUser();
            return ResponseEntity.ok(AuthResponse.builder()
                    .email(u.getEmail()).name(u.getName()).role(u.getRole().name()).build());
        }

        // Fallback to gateway-injected header
        if (gatewayEmail != null && !gatewayEmail.isBlank()) {
            User u = userRepository.findByEmail(gatewayEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "User not found: " + gatewayEmail));
            return ResponseEntity.ok(AuthResponse.builder()
                    .email(u.getEmail()).name(u.getName()).role(u.getRole().name()).build());
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
    }

    @PostMapping("/otp/send")
    @Operation(summary = "Send OTP to email")
    public ResponseEntity<MessageResponse> sendOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.sendOtp(request.getEmail()));
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP code")
    public ResponseEntity<MessageResponse> verifyOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request.getEmail(), request.getOtp()));
    }

    @PutMapping("/forgotPassword/{email}")
    public ResponseEntity<MessageResponse> forgotPassword(@PathVariable String email,
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(email, request));
    }

    @PutMapping("/resetPassword/{email}")
    public ResponseEntity<MessageResponse> resetPassword(@PathVariable String email,
            @RequestParam @NotBlank String currentPassword,
            @RequestParam @NotBlank @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@#$%^&*()+\\-=])(?=.*\\d).{8,}$") String newPassword) {
        return ResponseEntity.ok(authService.resetPassword(email, currentPassword, newPassword));
    }
}
