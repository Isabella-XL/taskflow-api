package com.xiaoxu.taskflow.service;

import com.xiaoxu.taskflow.entity.RefreshToken;
import com.xiaoxu.taskflow.entity.User;
import com.xiaoxu.taskflow.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    public RefreshToken createRefreshToken(User user) {

        // （推荐）一个用户只保留一个 refresh token
        repository.deleteByUser(user);

        repository.flush();

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        return repository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            repository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }
}