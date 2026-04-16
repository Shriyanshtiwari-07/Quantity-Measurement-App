package com.app.userservice.service;

import com.app.userservice.dto.request.*;
import com.app.userservice.dto.response.*;
import com.app.userservice.entity.*;
import com.app.userservice.enums.*;
import com.app.userservice.repository.*;
import com.app.userservice.security.UserPrincipal;
import com.app.userservice.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthenticationService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_OTP_PER_HOUR = 5;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepository,
            OtpTokenRepository otpTokenRepository, PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.otpTokenRepository = otpTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use.");
        User newUser = User.builder().email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName()).provider(AuthProvider.LOCAL).role(Role.USER).build();
        userRepository.save(newUser);
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtTokenProvider.generateToken(auth);
        UserPrincipal p = (UserPrincipal) auth.getPrincipal();
        emailService.sendRegistrationEmail(newUser.getEmail(), newUser.getName() != null ? newUser.getName() : "there");
        return AuthResponse.builder().accessToken(token).tokenType("Bearer")
                .email(p.getEmail()).name(p.getUser().getName()).role(p.getUser().getRole().name()).build();
    }

    public AuthResponse login(AuthRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            String token = jwtTokenProvider.generateToken(auth);
            UserPrincipal p = (UserPrincipal) auth.getPrincipal();
            emailService.sendLoginNotificationEmail(request.getEmail());
            return AuthResponse.builder().accessToken(token).tokenType("Bearer")
                    .email(p.getEmail()).name(p.getUser().getName()).role(p.getUser().getRole().name()).build();
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password!");
        }
    }

    @Transactional
    public MessageResponse sendOtp(String email) {
        if (!userRepository.existsByEmail(email))
            return new MessageResponse("If an account with that email exists, a verification code has been sent.");
        long recent = otpTokenRepository.countByEmailAndCreatedAtAfter(email, LocalDateTime.now().minusHours(1));
        if (recent >= MAX_OTP_PER_HOUR)
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests. Try again later.");
        String raw = String.valueOf(secureRandom.nextInt(900000) + 100000);
        OtpToken otp = OtpToken.builder().email(email).otpHash(passwordEncoder.encode(raw))
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)).build();
        otpTokenRepository.save(otp);
        emailService.sendOtpEmail(email, raw);
        return new MessageResponse("Verification code sent to your email.");
    }

    @Transactional
    public MessageResponse verifyOtp(String email, String otp) {
        OtpToken token = otpTokenRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No pending code found. Request a new one."));
        if (token.isExpired())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code expired. Request a new one.");
        if (token.isLockedOut())
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many failed attempts. Request a new code.");
        if (!passwordEncoder.matches(otp, token.getOtpHash())) {
            token.setAttempts(token.getAttempts() + 1);
            otpTokenRepository.save(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid code. " + (5 - token.getAttempts()) + " attempts left.");
        }
        token.setVerified(true);
        otpTokenRepository.save(token);
        return new MessageResponse("Verification code verified successfully.");
    }

    @Transactional
    public MessageResponse forgotPassword(String email, ForgotPasswordRequest request) {
        OtpToken verified = otpTokenRepository.findTopByEmailAndVerifiedTrueOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Email verification required."));
        if (verified.getExpiresAt().plusMinutes(10).isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Verification session expired.");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        otpTokenRepository.deleteAllByEmail(email);
        emailService.sendForgotPasswordEmail(email);
        return new MessageResponse("Password has been changed successfully!");
    }

    public MessageResponse resetPassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email));
        if (!passwordEncoder.matches(currentPassword, user.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect!");
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(email);
        return new MessageResponse("Password reset successfully!");
    }
}
