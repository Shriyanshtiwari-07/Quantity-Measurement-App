package com.app.userservice.repository;

import com.app.userservice.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(String email);
    Optional<OtpToken> findTopByEmailAndVerifiedTrueOrderByCreatedAtDesc(String email);
    void deleteAllByEmail(String email);
    long countByEmailAndCreatedAtAfter(String email, LocalDateTime after);
}
