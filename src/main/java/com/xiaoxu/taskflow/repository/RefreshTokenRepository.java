package com.xiaoxu.taskflow.repository;

import com.xiaoxu.taskflow.entity.RefreshToken;
import com.xiaoxu.taskflow.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}