package com.example.repositories;

import com.example.models.RefreshToken;
import com.example.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByToken(String token);

    @Transactional
    void deleteByUser(User user);
}
